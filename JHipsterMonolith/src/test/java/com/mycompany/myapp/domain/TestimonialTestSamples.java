package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestimonialTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Testimonial getTestimonialSample1() {
        return new Testimonial().id(1L).name("name1").rating(1);
    }

    public static Testimonial getTestimonialSample2() {
        return new Testimonial().id(2L).name("name2").rating(2);
    }

    public static Testimonial getTestimonialRandomSampleGenerator() {
        return new Testimonial().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).rating(intCount.incrementAndGet());
    }
}
