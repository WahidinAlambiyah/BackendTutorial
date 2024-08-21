package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RegionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Region getRegionSample1() {
        return new Region().id(1L).name("name1").unm49Code("unm49Code1").isoAlpha2Code("isoAlpha2Code1");
    }

    public static Region getRegionSample2() {
        return new Region().id(2L).name("name2").unm49Code("unm49Code2").isoAlpha2Code("isoAlpha2Code2");
    }

    public static Region getRegionRandomSampleGenerator() {
        return new Region()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .unm49Code(UUID.randomUUID().toString())
            .isoAlpha2Code(UUID.randomUUID().toString());
    }
}
