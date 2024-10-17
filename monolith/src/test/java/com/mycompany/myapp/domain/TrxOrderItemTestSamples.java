package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TrxOrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TrxOrderItem getTrxOrderItemSample1() {
        return new TrxOrderItem().id(1L).quantity(1);
    }

    public static TrxOrderItem getTrxOrderItemSample2() {
        return new TrxOrderItem().id(2L).quantity(2);
    }

    public static TrxOrderItem getTrxOrderItemRandomSampleGenerator() {
        return new TrxOrderItem().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
