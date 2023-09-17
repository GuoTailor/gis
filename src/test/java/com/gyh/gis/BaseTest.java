package com.gyh.gis;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

/**
 * create by GYH on 2022/9/27
 */
public class BaseTest {
    @Test
    public void testTime() throws InterruptedException {

        HashMap<LocalDate, LocalDateTime> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            System.out.println(map.size());
            LocalDateTime now = LocalDateTime.now();
            map.put(now.toLocalDate(), now);
            Thread.sleep(1_000);
        }
        System.out.println(map);
    }

    @Test
    public void testStream() {
        List<Integer> integers = List.of(1, 2, 3, 5, 6, 7, 8, 9, 10);
        integers.parallelStream()
                .map(it -> {
                    String name = Thread.currentThread().getName();
                    System.out.println(name + "--" + it);
                    return it;
                })
                .forEach(it -> {
                    String name = Thread.currentThread().getName();
                    System.out.println(name + " " + it);
                });
    }

    @Test
    public void testBase() {
        byte[] body = new byte[]{0x02, 0x06, 0x6A};
        int length = body[0];
        int data = 0;
        for (int i = 1; i <= length; i++) {
            data = (data << 8) + body[ i];
        }
        System.out.println(data);
    }

    @Test
    public void testTime2() throws ParseException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH-mm-ss-");
        System.out.println(fmt.format(LocalDateTime.now()));
//        System.out.println(df.parse("2022-10-12 13:00:51").getTime());
    }
}
