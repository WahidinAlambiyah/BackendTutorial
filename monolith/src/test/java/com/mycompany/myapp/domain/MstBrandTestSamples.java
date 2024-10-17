package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MstBrandTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MstBrand getMstBrandSample1() {
        return new MstBrand().id(1L).name("name1").logo("logo1").description("description1");
    }

    public static MstBrand getMstBrandSample2() {
        return new MstBrand().id(2L).name("name2").logo("logo2").description("description2");
    }

    public static MstBrand getMstBrandRandomSampleGenerator() {
        return new MstBrand()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .logo(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
