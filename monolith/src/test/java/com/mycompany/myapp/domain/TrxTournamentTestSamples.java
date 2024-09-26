package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TrxTournamentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TrxTournament getTrxTournamentSample1() {
        return new TrxTournament().id(1L).name("name1").location("location1").maxParticipants(1);
    }

    public static TrxTournament getTrxTournamentSample2() {
        return new TrxTournament().id(2L).name("name2").location("location2").maxParticipants(2);
    }

    public static TrxTournament getTrxTournamentRandomSampleGenerator() {
        return new TrxTournament()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString())
            .maxParticipants(intCount.incrementAndGet());
    }
}
