package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PostalCodeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PostalCode getPostalCodeSample1() {
        return new PostalCode().id(1L).code("code1");
    }

    public static PostalCode getPostalCodeSample2() {
        return new PostalCode().id(2L).code("code2");
    }

    public static PostalCode getPostalCodeRandomSampleGenerator() {
        return new PostalCode().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString());
    }
}
