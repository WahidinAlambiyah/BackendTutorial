package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TrxTestimonialTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TrxTestimonial getTrxTestimonialSample1() {
        return new TrxTestimonial().id(1L).name("name1").rating(1);
    }

    public static TrxTestimonial getTrxTestimonialSample2() {
        return new TrxTestimonial().id(2L).name("name2").rating(2);
    }

    public static TrxTestimonial getTrxTestimonialRandomSampleGenerator() {
        return new TrxTestimonial().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).rating(intCount.incrementAndGet());
    }
}
