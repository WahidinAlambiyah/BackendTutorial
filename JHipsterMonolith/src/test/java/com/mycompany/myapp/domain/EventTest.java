package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EventTestSamples.*;
import static com.mycompany.myapp.domain.ServiceTestSamples.*;
import static com.mycompany.myapp.domain.TestimonialTestSamples.*;
import static com.mycompany.myapp.domain.TournamentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = getEventSample1();
        Event event2 = new Event();
        assertThat(event1).isNotEqualTo(event2);

        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);

        event2 = getEventSample2();
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void tournamentTest() {
        Event event = getEventRandomSampleGenerator();
        Tournament tournamentBack = getTournamentRandomSampleGenerator();

        event.addTournament(tournamentBack);
        assertThat(event.getTournaments()).containsOnly(tournamentBack);
        assertThat(tournamentBack.getEvent()).isEqualTo(event);

        event.removeTournament(tournamentBack);
        assertThat(event.getTournaments()).doesNotContain(tournamentBack);
        assertThat(tournamentBack.getEvent()).isNull();

        event.tournaments(new HashSet<>(Set.of(tournamentBack)));
        assertThat(event.getTournaments()).containsOnly(tournamentBack);
        assertThat(tournamentBack.getEvent()).isEqualTo(event);

        event.setTournaments(new HashSet<>());
        assertThat(event.getTournaments()).doesNotContain(tournamentBack);
        assertThat(tournamentBack.getEvent()).isNull();
    }

    @Test
    void serviceTest() {
        Event event = getEventRandomSampleGenerator();
        Services serviceBack = getServiceRandomSampleGenerator();

        event.setService(serviceBack);
        assertThat(event.getService()).isEqualTo(serviceBack);

        event.service(null);
        assertThat(event.getService()).isNull();
    }

    @Test
    void testimonialTest() {
        Event event = getEventRandomSampleGenerator();
        Testimonial testimonialBack = getTestimonialRandomSampleGenerator();

        event.setTestimonial(testimonialBack);
        assertThat(event.getTestimonial()).isEqualTo(testimonialBack);

        event.testimonial(null);
        assertThat(event.getTestimonial()).isNull();
    }
}
