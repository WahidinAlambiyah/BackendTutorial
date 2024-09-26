package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MstJobTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MstJob getMstJobSample1() {
        return new MstJob().id(1L).jobTitle("jobTitle1").minSalary(1L).maxSalary(1L);
    }

    public static MstJob getMstJobSample2() {
        return new MstJob().id(2L).jobTitle("jobTitle2").minSalary(2L).maxSalary(2L);
    }

    public static MstJob getMstJobRandomSampleGenerator() {
        return new MstJob()
            .id(longCount.incrementAndGet())
            .jobTitle(UUID.randomUUID().toString())
            .minSalary(longCount.incrementAndGet())
            .maxSalary(longCount.incrementAndGet());
    }
}
