package com.gyh.gis.collect;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.Greeting;
import com.gyh.gis.service.StationService;
import com.gyh.gis.service.WebSocket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
