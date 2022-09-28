package com.gyh.gis;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * create by GYH on 2022/9/27
 */
public class BaseTest {
    @Test
    public void testTime() {
        System.out.println(LocalDate.now());
        System.out.println(LocalDate.now().atStartOfDay());
        System.out.println(LocalDate.now().atTime(LocalTime.MAX));
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
}
