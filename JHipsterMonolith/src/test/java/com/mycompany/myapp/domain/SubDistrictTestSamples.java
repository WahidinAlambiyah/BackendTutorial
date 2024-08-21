package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubDistrictTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SubDistrict getSubDistrictSample1() {
        return new SubDistrict().id(1L).name("name1").code("code1");
    }

    public static SubDistrict getSubDistrictSample2() {
        return new SubDistrict().id(2L).name("name2").code("code2");
    }

    public static SubDistrict getSubDistrictRandomSampleGenerator() {
        return new SubDistrict().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).code(UUID.randomUUID().toString());
    }
}
