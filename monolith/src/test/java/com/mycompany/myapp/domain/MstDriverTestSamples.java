package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MstDriverTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MstDriver getMstDriverSample1() {
        return new MstDriver().id(1L).name("name1").contactNumber("contactNumber1").vehicleDetails("vehicleDetails1");
    }

    public static MstDriver getMstDriverSample2() {
        return new MstDriver().id(2L).name("name2").contactNumber("contactNumber2").vehicleDetails("vehicleDetails2");
    }

    public static MstDriver getMstDriverRandomSampleGenerator() {
        return new MstDriver()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .contactNumber(UUID.randomUUID().toString())
            .vehicleDetails(UUID.randomUUID().toString());
    }
}
