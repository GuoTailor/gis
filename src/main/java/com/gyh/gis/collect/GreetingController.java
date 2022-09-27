package com.gyh.gis.collect;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.gis.domain.Station;
import com.gyh.gis.domain.Test;
import com.gyh.gis.dto.Greeting;
import com.gyh.gis.mapper.TestMapper;
import com.gyh.gis.service.StationService;
import com.gyh.gis.service.WebSocket;
import com.gyh.gis.support.shardingtable.executor.DetermineTableNameForNewExe;
import com.gyh.gis.support.shardingtable.executor.input.DetermineTableNameForNewInput;
import com.gyh.gis.support.shardingtable.executor.output.DetermineTableNameForNewOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * create by GYH on 2022/9/21
 */
@Slf4j
@RestController
@Tag(name = "测试")
public class GreetingController {
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StationService stationService;
    @Resource
    private TestMapper testMapper;
    @Autowired
    private DetermineTableNameForNewExe determineTableNameForNewExe;

    @Operation(summary = "发送socket消息")
    @GetMapping("/send")
    public Greeting greeting(@RequestParam String message) {
        webSocket.sendMsgByAll(message);
        Greeting greeting = new Greeting("Hello, " + message + "!");
        redisTemplate.opsForHash().put("test", "1", greeting);
        Greeting g = redisTemplate.<String, Greeting>opsForHash().get("test", "1");
        log.info(g.getClass().getName());
        return g;
    }

    @Operation(summary = "test")
    @GetMapping("/getPage")
    public Page<Station> getPage() {
        return stationService.page();
    }

    @Operation(summary = "testFy")
    @GetMapping("/testFy")
    public int testFy() {
        DetermineTableNameForNewOutput execute = determineTableNameForNewExe.execute(new DetermineTableNameForNewInput()
                .setOriginTableName(Test.class)
                .setCreateTime(LocalDateTime.now()));
        String tableName = execute.getTableName();
        Test test = new Test();
        test.setAge((int) (Math.random() * 100));
        test.setName("" + (Math.random() * 100));
        return testMapper.insertSelective(test, tableName);
    }
}
