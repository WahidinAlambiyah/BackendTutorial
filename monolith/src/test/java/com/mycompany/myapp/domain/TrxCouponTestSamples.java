package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TrxCouponTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrxCoupon getTrxCouponSample1() {
        return new TrxCoupon().id(1L).code("code1");
    }

    public static TrxCoupon getTrxCouponSample2() {
        return new TrxCoupon().id(2L).code("code2");
    }

    public static TrxCoupon getTrxCouponRandomSampleGenerator() {
        return new TrxCoupon().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString());
    }
}
