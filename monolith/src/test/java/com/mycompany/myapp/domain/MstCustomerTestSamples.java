package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MstCustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MstCustomer getMstCustomerSample1() {
        return new MstCustomer()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .phoneNumber("phoneNumber1")
            .address("address1")
            .loyaltyPoints(1);
    }

    public static MstCustomer getMstCustomerSample2() {
        return new MstCustomer()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .phoneNumber("phoneNumber2")
            .address("address2")
            .loyaltyPoints(2);
    }

    public static MstCustomer getMstCustomerRandomSampleGenerator() {
        return new MstCustomer()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .loyaltyPoints(intCount.incrementAndGet());
    }
}
