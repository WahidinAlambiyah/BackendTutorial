package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MstEmployeeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MstEmployee getMstEmployeeSample1() {
        return new MstEmployee()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .phoneNumber("phoneNumber1")
            .salary(1L)
            .commissionPct(1L);
    }

    public static MstEmployee getMstEmployeeSample2() {
        return new MstEmployee()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .phoneNumber("phoneNumber2")
            .salary(2L)
            .commissionPct(2L);
    }

    public static MstEmployee getMstEmployeeRandomSampleGenerator() {
        return new MstEmployee()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .salary(longCount.incrementAndGet())
            .commissionPct(longCount.incrementAndGet());
    }
}
