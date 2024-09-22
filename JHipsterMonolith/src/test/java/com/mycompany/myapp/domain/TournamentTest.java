package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EventTestSamples.*;
import static com.mycompany.myapp.domain.TournamentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TournamentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tournament.class);
        Tournament tournament1 = getTournamentSample1();
        Tournament tournament2 = new Tournament();
        assertThat(tournament1).isNotEqualTo(tournament2);

        tournament2.setId(tournament1.getId());
        assertThat(tournament1).isEqualTo(tournament2);

        tournament2 = getTournamentSample2();
        assertThat(tournament1).isNotEqualTo(tournament2);
    }

    @Test
    void eventTest() {
        Tournament tournament = getTournamentRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        tournament.setEvent(eventBack);
        assertThat(tournament.getEvent()).isEqualTo(eventBack);

        tournament.event(null);
        assertThat(tournament.getEvent()).isNull();
    }
}
