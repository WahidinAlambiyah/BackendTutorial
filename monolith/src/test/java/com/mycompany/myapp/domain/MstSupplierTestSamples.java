package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MstSupplierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MstSupplier getMstSupplierSample1() {
        return new MstSupplier().id(1L).name("name1").contactInfo("contactInfo1").address("address1").rating(1);
    }

    public static MstSupplier getMstSupplierSample2() {
        return new MstSupplier().id(2L).name("name2").contactInfo("contactInfo2").address("address2").rating(2);
    }

    public static MstSupplier getMstSupplierRandomSampleGenerator() {
        return new MstSupplier()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .contactInfo(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .rating(intCount.incrementAndGet());
    }
}
