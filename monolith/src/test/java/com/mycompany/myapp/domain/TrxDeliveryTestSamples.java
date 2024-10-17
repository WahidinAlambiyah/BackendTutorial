package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TrxDeliveryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrxDelivery getTrxDeliverySample1() {
        return new TrxDelivery().id(1L).deliveryAddress("deliveryAddress1").assignedDriver("assignedDriver1");
    }

    public static TrxDelivery getTrxDeliverySample2() {
        return new TrxDelivery().id(2L).deliveryAddress("deliveryAddress2").assignedDriver("assignedDriver2");
    }

    public static TrxDelivery getTrxDeliveryRandomSampleGenerator() {
        return new TrxDelivery()
            .id(longCount.incrementAndGet())
            .deliveryAddress(UUID.randomUUID().toString())
            .assignedDriver(UUID.randomUUID().toString());
    }
}
