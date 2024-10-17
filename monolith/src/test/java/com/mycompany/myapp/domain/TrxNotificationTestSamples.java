package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TrxNotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrxNotification getTrxNotificationSample1() {
        return new TrxNotification().id(1L).recipient("recipient1").messageType("messageType1").content("content1");
    }

    public static TrxNotification getTrxNotificationSample2() {
        return new TrxNotification().id(2L).recipient("recipient2").messageType("messageType2").content("content2");
    }

    public static TrxNotification getTrxNotificationRandomSampleGenerator() {
        return new TrxNotification()
            .id(longCount.incrementAndGet())
            .recipient(UUID.randomUUID().toString())
            .messageType(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString());
    }
}
