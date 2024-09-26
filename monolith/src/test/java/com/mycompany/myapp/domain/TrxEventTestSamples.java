package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TrxEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TrxEvent getTrxEventSample1() {
        return new TrxEvent().id(1L).title("title1").location("location1").capacity(1);
    }

    public static TrxEvent getTrxEventSample2() {
        return new TrxEvent().id(2L).title("title2").location("location2").capacity(2);
    }

    public static TrxEvent getTrxEventRandomSampleGenerator() {
        return new TrxEvent()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString())
            .capacity(intCount.incrementAndGet());
    }
}
