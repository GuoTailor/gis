package com.gyh.gis.config;

import com.gyh.gis.service.DefaultInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * create by GYH on 2022/9/21
 */
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private WebSocketHandler defaultHandler;
    @Autowired
    private DefaultInterceptor defaultInterceptor;;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(defaultHandler, "/ws")
                .addInterceptors(defaultInterceptor)
                .setAllowedOrigins("*"); // 解决跨域问题
    }
}
