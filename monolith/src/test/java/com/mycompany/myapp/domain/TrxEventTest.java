package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstServiceTestSamples.*;
import static com.mycompany.myapp.domain.TrxEventTestSamples.*;
import static com.mycompany.myapp.domain.TrxTestimonialTestSamples.*;
import static com.mycompany.myapp.domain.TrxTournamentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TrxEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxEvent.class);
        TrxEvent trxEvent1 = getTrxEventSample1();
        TrxEvent trxEvent2 = new TrxEvent();
        assertThat(trxEvent1).isNotEqualTo(trxEvent2);

        trxEvent2.setId(trxEvent1.getId());
        assertThat(trxEvent1).isEqualTo(trxEvent2);

        trxEvent2 = getTrxEventSample2();
        assertThat(trxEvent1).isNotEqualTo(trxEvent2);
    }

    @Test
    void tournamentTest() {
        TrxEvent trxEvent = getTrxEventRandomSampleGenerator();
        TrxTournament trxTournamentBack = getTrxTournamentRandomSampleGenerator();

        trxEvent.addTournament(trxTournamentBack);
        assertThat(trxEvent.getTournaments()).containsOnly(trxTournamentBack);
        assertThat(trxTournamentBack.getEvent()).isEqualTo(trxEvent);

        trxEvent.removeTournament(trxTournamentBack);
        assertThat(trxEvent.getTournaments()).doesNotContain(trxTournamentBack);
        assertThat(trxTournamentBack.getEvent()).isNull();

        trxEvent.tournaments(new HashSet<>(Set.of(trxTournamentBack)));
        assertThat(trxEvent.getTournaments()).containsOnly(trxTournamentBack);
        assertThat(trxTournamentBack.getEvent()).isEqualTo(trxEvent);

        trxEvent.setTournaments(new HashSet<>());
        assertThat(trxEvent.getTournaments()).doesNotContain(trxTournamentBack);
        assertThat(trxTournamentBack.getEvent()).isNull();
    }

    @Test
    void serviceTest() {
        TrxEvent trxEvent = getTrxEventRandomSampleGenerator();
        MstService mstServiceBack = getMstServiceRandomSampleGenerator();

        trxEvent.setService(mstServiceBack);
        assertThat(trxEvent.getService()).isEqualTo(mstServiceBack);

        trxEvent.service(null);
        assertThat(trxEvent.getService()).isNull();
    }

    @Test
    void testimonialTest() {
        TrxEvent trxEvent = getTrxEventRandomSampleGenerator();
        TrxTestimonial trxTestimonialBack = getTrxTestimonialRandomSampleGenerator();

        trxEvent.setTestimonial(trxTestimonialBack);
        assertThat(trxEvent.getTestimonial()).isEqualTo(trxTestimonialBack);

        trxEvent.testimonial(null);
        assertThat(trxEvent.getTestimonial()).isNull();
    }
}
