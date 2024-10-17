package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MstCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MstCategory getMstCategorySample1() {
        return new MstCategory().id(1L).name("name1").description("description1");
    }

    public static MstCategory getMstCategorySample2() {
        return new MstCategory().id(2L).name("name2").description("description2");
    }

    public static MstCategory getMstCategoryRandomSampleGenerator() {
        return new MstCategory()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
