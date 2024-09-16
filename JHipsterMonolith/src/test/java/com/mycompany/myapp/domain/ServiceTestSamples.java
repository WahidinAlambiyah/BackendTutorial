package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Services getServiceSample1() {
        return new Services().id(1L).name("name1").durationInHours(1);
    }

    public static Services getServiceSample2() {
        return new Services().id(2L).name("name2").durationInHours(2);
    }

    public static Services getServiceRandomSampleGenerator() {
        return new Services().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).durationInHours(intCount.incrementAndGet());
    }
}
