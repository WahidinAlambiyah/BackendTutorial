package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TrxOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrxOrder getTrxOrderSample1() {
        return new TrxOrder().id(1L);
    }

    public static TrxOrder getTrxOrderSample2() {
        return new TrxOrder().id(2L);
    }

    public static TrxOrder getTrxOrderRandomSampleGenerator() {
        return new TrxOrder().id(longCount.incrementAndGet());
    }
}
