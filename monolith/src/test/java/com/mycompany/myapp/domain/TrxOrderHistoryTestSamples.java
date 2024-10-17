package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TrxOrderHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrxOrderHistory getTrxOrderHistorySample1() {
        return new TrxOrderHistory().id(1L);
    }

    public static TrxOrderHistory getTrxOrderHistorySample2() {
        return new TrxOrderHistory().id(2L);
    }

    public static TrxOrderHistory getTrxOrderHistoryRandomSampleGenerator() {
        return new TrxOrderHistory().id(longCount.incrementAndGet());
    }
}
