package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MstLoyaltyProgramTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MstLoyaltyProgram getMstLoyaltyProgramSample1() {
        return new MstLoyaltyProgram().id(1L).pointsEarned(1).membershipTier("membershipTier1");
    }

    public static MstLoyaltyProgram getMstLoyaltyProgramSample2() {
        return new MstLoyaltyProgram().id(2L).pointsEarned(2).membershipTier("membershipTier2");
    }

    public static MstLoyaltyProgram getMstLoyaltyProgramRandomSampleGenerator() {
        return new MstLoyaltyProgram()
            .id(longCount.incrementAndGet())
            .pointsEarned(intCount.incrementAndGet())
            .membershipTier(UUID.randomUUID().toString());
    }
}
