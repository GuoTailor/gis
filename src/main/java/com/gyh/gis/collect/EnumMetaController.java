package com.gyh.gis.collect;

import com.gyh.gis.dto.ResponseInfo;
import com.gyh.gis.util.EnumScannerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@RestController
@RequestMapping("/anon/enumMeta")
@Tag(name = "枚举信息")
public class EnumMetaController {

    private static final Map<String, LinkedHashMap<String, Enum<?>>> ENUM_MAP = new HashMap<>();

    @SneakyThrows
    @PostConstruct
    public void init() {
        String[] p = new String[]{"com.gyh.gis"};
        Set<Class<?>> enumClass = EnumScannerUtils.scan(p, Enum.class);

        for (Class<?> clazz : enumClass) {
            if (!Modifier.isPublic(clazz.getModifiers())) {
                // 只扫描public的枚举
                continue;
            }
            Method method = clazz.getMethod("values");
            Enum<?>[] enums = (Enum<?>[]) method.invoke(null);
            LinkedHashMap<String, Enum<?>> map = new LinkedHashMap<>();
            for (Enum<?> anEnum : enums) {
                map.put(anEnum.name(), anEnum);
            }
            ENUM_MAP.put(clazz.getSimpleName(), map);
        }

    }

    @GetMapping("/getEnumBySimpleName/{simpleName}")
    @Operation(summary = "通过类名获取枚举列表")
    @Parameters({
            @Parameter(name = "simpleName", description = "类名列表，逗号分隔", example = "TaxControlType,OrderStatus", required = true)})
    public ResponseInfo<Map<String, LinkedHashMap<String, Enum<?>>>> getEnumBySimpleName(@PathVariable("simpleName") List<String> simpleName) {
        Map<String, LinkedHashMap<String, Enum<?>>> resultMap = new HashMap<>();
        for (String name : simpleName) {
            LinkedHashMap<String, Enum<?>> enumMap = ENUM_MAP.get(name);
            resultMap.put(name, enumMap);
        }
        return ResponseInfo.ok(resultMap);
    }
}
