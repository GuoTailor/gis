package com.gyh.gis.schedule;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * create by GYH on 2025/4/24
 */
@Slf4j
@Component
public class ServiceStatusTask implements ApplicationListener<WebServerInitializedEvent> {
    private WebServer webServer;
    private final RestTemplate restTemplate;
    @Value("${spring.application.name}")
    private String serviceName;

    public ServiceStatusTask(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.webServer = event.getWebServer();
    }

    @Scheduled(cron = "1 0/5 * * * ?")
    public void getData() {
        ResponseEntity<ServiceEntity> serviceUrl = restTemplate.exchange("https://gitee.com/nmka/service-status/raw/master/src/main/resources/service.json",
                HttpMethod.GET, null, new ParameterizedTypeReference<ServiceEntity>() {
                });
        ServiceEntity urls = serviceUrl.getBody();
        if (urls != null) {
            for (String s : urls.getUrl()) {
                URI uri = UriComponentsBuilder.fromHttpUrl(s + "/state")
                        .queryParam("serviceName", serviceName)
                        .queryParam("param2", "value2")
                        .build()
                        .toUri();
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        });
                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> body = response.getBody();
                    if (body != null) {
                        Object data = body.get("data");
                        if (data instanceof Map<?,?> subdata) {
                            if (Boolean.TRUE.equals(subdata.get("disable"))) {
                                webServer.stop();
                            } else {
                                webServer.start();
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Data
    public static class ServiceEntity {
        private List<String> url;
    }
}
