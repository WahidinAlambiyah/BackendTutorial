package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Event getEventSample1() {
        return new Event().id(1L).title("title1").location("location1").capacity(1);
    }

    public static Event getEventSample2() {
        return new Event().id(2L).title("title2").location("location2").capacity(2);
    }

    public static Event getEventRandomSampleGenerator() {
        return new Event()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString())
            .capacity(intCount.incrementAndGet());
    }
}
