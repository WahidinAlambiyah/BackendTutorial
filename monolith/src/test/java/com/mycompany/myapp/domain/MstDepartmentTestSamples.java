package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MstDepartmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MstDepartment getMstDepartmentSample1() {
        return new MstDepartment().id(1L).departmentName("departmentName1");
    }

    public static MstDepartment getMstDepartmentSample2() {
        return new MstDepartment().id(2L).departmentName("departmentName2");
    }

    public static MstDepartment getMstDepartmentRandomSampleGenerator() {
        return new MstDepartment().id(longCount.incrementAndGet()).departmentName(UUID.randomUUID().toString());
    }
}
