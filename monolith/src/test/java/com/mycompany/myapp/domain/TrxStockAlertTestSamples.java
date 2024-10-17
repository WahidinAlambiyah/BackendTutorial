package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TrxStockAlertTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TrxStockAlert getTrxStockAlertSample1() {
        return new TrxStockAlert().id(1L).alertThreshold(1).currentStock(1);
    }

    public static TrxStockAlert getTrxStockAlertSample2() {
        return new TrxStockAlert().id(2L).alertThreshold(2).currentStock(2);
    }

    public static TrxStockAlert getTrxStockAlertRandomSampleGenerator() {
        return new TrxStockAlert()
            .id(longCount.incrementAndGet())
            .alertThreshold(intCount.incrementAndGet())
            .currentStock(intCount.incrementAndGet());
    }
}
