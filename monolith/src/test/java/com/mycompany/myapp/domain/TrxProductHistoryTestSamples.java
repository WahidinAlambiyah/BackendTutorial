package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TrxProductHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrxProductHistory getTrxProductHistorySample1() {
        return new TrxProductHistory().id(1L);
    }

    public static TrxProductHistory getTrxProductHistorySample2() {
        return new TrxProductHistory().id(2L);
    }

    public static TrxProductHistory getTrxProductHistoryRandomSampleGenerator() {
        return new TrxProductHistory().id(longCount.incrementAndGet());
    }
}
