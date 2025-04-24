package com.gyh.gis.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * create by GYH on 2025/4/24
 */
@Slf4j
@Component
public class ServiceStatusTask implements ApplicationListener<WebServerInitializedEvent> {
    private WebServer webServer;
    private final RestClient restClient;

    public ServiceStatusTask(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://example.org").build();;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.webServer = event.getWebServer();
    }

    @Scheduled(cron = "1 0/5 * * * ?")
    public void getData() {
        Map<String, Object> body = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/data") // 指定路径
                        .queryParam("param1", 1) // 添加 Query 参数
                        .queryParam("param2", "param2") // 添加另一个 Query 参数
                        .build())
                .retrieve().body(Map.class);
        log.info("开始关闭");
        webServer.stop();
    }
}
