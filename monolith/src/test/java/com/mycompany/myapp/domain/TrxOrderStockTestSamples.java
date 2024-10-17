package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TrxOrderStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TrxOrderStock getTrxOrderStockSample1() {
        return new TrxOrderStock().id(1L).quantityOrdered(1);
    }

    public static TrxOrderStock getTrxOrderStockSample2() {
        return new TrxOrderStock().id(2L).quantityOrdered(2);
    }

    public static TrxOrderStock getTrxOrderStockRandomSampleGenerator() {
        return new TrxOrderStock().id(longCount.incrementAndGet()).quantityOrdered(intCount.incrementAndGet());
    }
}
