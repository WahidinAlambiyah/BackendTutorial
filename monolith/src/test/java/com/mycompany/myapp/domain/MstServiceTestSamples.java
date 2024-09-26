package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MstServiceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MstService getMstServiceSample1() {
        return new MstService().id(1L).name("name1").durationInHours(1);
    }

    public static MstService getMstServiceSample2() {
        return new MstService().id(2L).name("name2").durationInHours(2);
    }

    public static MstService getMstServiceRandomSampleGenerator() {
        return new MstService()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .durationInHours(intCount.incrementAndGet());
    }
}
