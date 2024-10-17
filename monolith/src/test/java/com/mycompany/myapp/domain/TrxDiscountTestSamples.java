package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TrxDiscountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrxDiscount getTrxDiscountSample1() {
        return new TrxDiscount().id(1L);
    }

    public static TrxDiscount getTrxDiscountSample2() {
        return new TrxDiscount().id(2L);
    }

    public static TrxDiscount getTrxDiscountRandomSampleGenerator() {
        return new TrxDiscount().id(longCount.incrementAndGet());
    }
}
