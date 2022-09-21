package com.gyh.gis.collect;

import com.gyh.gis.dto.Greeting;
import com.gyh.gis.service.WebSocket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * create by GYH on 2022/9/21
 */
@RestController
@Tag(name = "测试")
public class GreetingController {
    @Autowired
    private WebSocket webSocket;

    @Operation(summary = "发送socket消息")
    @GetMapping("/send")
    public Greeting greeting(@RequestParam String message) {
        webSocket.sendMsgByAll(message);
        return new Greeting("Hello, " + message + "!");
    }
}
