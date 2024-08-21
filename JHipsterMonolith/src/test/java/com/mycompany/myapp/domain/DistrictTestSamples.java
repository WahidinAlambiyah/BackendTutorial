package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DistrictTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static District getDistrictSample1() {
        return new District().id(1L).name("name1").code("code1");
    }

    public static District getDistrictSample2() {
        return new District().id(2L).name("name2").code("code2");
    }

    public static District getDistrictRandomSampleGenerator() {
        return new District().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).code(UUID.randomUUID().toString());
    }
}
