package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProvinceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Province getProvinceSample1() {
        return new Province().id(1L).name("name1").code("code1");
    }

    public static Province getProvinceSample2() {
        return new Province().id(2L).name("name2").code("code2");
    }

    public static Province getProvinceRandomSampleGenerator() {
        return new Province().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).code(UUID.randomUUID().toString());
    }
}
