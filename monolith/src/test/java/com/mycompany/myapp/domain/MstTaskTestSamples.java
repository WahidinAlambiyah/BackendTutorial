package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MstTaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MstTask getMstTaskSample1() {
        return new MstTask().id(1L).title("title1").description("description1");
    }

    public static MstTask getMstTaskSample2() {
        return new MstTask().id(2L).title("title2").description("description2");
    }

    public static MstTask getMstTaskRandomSampleGenerator() {
        return new MstTask().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
