package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TrxCartTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrxCart getTrxCartSample1() {
        return new TrxCart().id(1L);
    }

    public static TrxCart getTrxCartSample2() {
        return new TrxCart().id(2L);
    }

    public static TrxCart getTrxCartRandomSampleGenerator() {
        return new TrxCart().id(longCount.incrementAndGet());
    }
}
