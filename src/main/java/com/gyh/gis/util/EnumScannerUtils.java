package com.gyh.gis.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
public class EnumScannerUtils {
    private static final String RESOURCE_PATTERN = "**/%s/**/*.class";

    public EnumScannerUtils() {
    }

    public static Set<Class<?>> scan(String[] confPkgs, Class<?> clazz) {
        Set<Class<?>> resClazzSet = new HashSet<>();
        List<AssignableTypeFilter> typeFilters = new LinkedList<>();
        typeFilters.add(new AssignableTypeFilter(clazz));

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        if (ArrayUtils.isNotEmpty(confPkgs)) {
            for (String pkg : confPkgs) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + String.format(RESOURCE_PATTERN, ClassUtils.convertClassNameToResourcePath(pkg));
                try {
                    Resource[] resources = resourcePatternResolver.getResources(pattern);
                    MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                    for (Resource resource : resources) {
                        if (resource.isReadable()) {
                            MetadataReader reader = readerFactory.getMetadataReader(resource);
                            String className = reader.getClassMetadata().getClassName();
                            if (ifMatchesEntityType(reader, readerFactory, typeFilters)) {
                                Class<?> curClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                                resClazzSet.add(curClass);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("扫描提取[{}]包路径下，标记了注解[{}]的类出现异常", pattern, StringUtils.join(typeFilters, ","));
                }
            }
        }
        return resClazzSet;
    }

    private static boolean ifMatchesEntityType(MetadataReader reader, MetadataReaderFactory readerFactory, List<AssignableTypeFilter> typeFilters) {
        if (!CollectionUtils.isEmpty(typeFilters)) {
            for (TypeFilter filter : typeFilters) {
                try {
                    if (filter.match(reader, readerFactory)) {
                        return true;
                    }
                } catch (IOException e) {
                    log.error("过滤匹配类型时出错 {}", e.getMessage());
                }
            }
        }
        return false;
    }
}