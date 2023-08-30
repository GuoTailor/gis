package com.gyh.gis.service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * create by GYH on 2022/9/21
 */
@Component
@Slf4j
public class WebSocket implements WebSocketHandler {
    private double random = Math.random();

    private final LinkedBlockingQueue<WebSocketSession> sessions = new LinkedBlockingQueue<>();

    /**
     * 建立连接
     *
     * @param session {@link WebSocketSession}
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws InterruptedException {
        log.info("建立连接" + random);
        sessions.put(session);
    }

    /**
     * 接收消息
     *
     * @param session {@link WebSocketSession}
     * @param message {@link WebSocketMessage}
     */
    @Override
    public void handleMessage(@NotNull WebSocketSession session, @NotNull WebSocketMessage<?> message) {
        log.info("接收消息 {}", message);
        log.info("接收消息 {}", message.getPayload());
    }

    /**
     * 发生错误
     *
     * @param session   {@link WebSocketSession}
     * @param exception 错误
     */
    @Override
    public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) {
        sessions.remove(session);
        log.info("发生错误 {}", exception.getMessage());
    }

    /**
     * 关闭连接
     *
     * @param session     {@link WebSocketSession}
     * @param closeStatus 关闭原因
     */
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, CloseStatus closeStatus) {
        sessions.remove(session);
        log.info("关闭连接 {}", closeStatus.toString());
    }

    /**
     * 是否支持发送部分消息
     *
     * @return 支持
     */
    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    public void sendMsgByAll(String msg) {
        sessions.forEach(it -> {
            try {
                it.sendMessage(new TextMessage(msg));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
