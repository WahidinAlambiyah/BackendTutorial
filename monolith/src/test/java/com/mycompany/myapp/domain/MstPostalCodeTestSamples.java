package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MstPostalCodeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MstPostalCode getMstPostalCodeSample1() {
        return new MstPostalCode().id(1L).code("code1");
    }

    public static MstPostalCode getMstPostalCodeSample2() {
        return new MstPostalCode().id(2L).code("code2");
    }

    public static MstPostalCode getMstPostalCodeRandomSampleGenerator() {
        return new MstPostalCode().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString());
    }
}
