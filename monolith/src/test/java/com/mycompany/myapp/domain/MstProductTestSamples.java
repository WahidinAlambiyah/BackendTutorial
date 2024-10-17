package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MstProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MstProduct getMstProductSample1() {
        return new MstProduct().id(1L).name("name1").description("description1").quantity(1).barcode("barcode1").unitSize("unitSize1");
    }

    public static MstProduct getMstProductSample2() {
        return new MstProduct().id(2L).name("name2").description("description2").quantity(2).barcode("barcode2").unitSize("unitSize2");
    }

    public static MstProduct getMstProductRandomSampleGenerator() {
        return new MstProduct()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .quantity(intCount.incrementAndGet())
            .barcode(UUID.randomUUID().toString())
            .unitSize(UUID.randomUUID().toString());
    }
}
