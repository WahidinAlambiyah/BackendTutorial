package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TrxEventTestSamples.*;
import static com.mycompany.myapp.domain.TrxTournamentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxTournamentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxTournament.class);
        TrxTournament trxTournament1 = getTrxTournamentSample1();
        TrxTournament trxTournament2 = new TrxTournament();
        assertThat(trxTournament1).isNotEqualTo(trxTournament2);

        trxTournament2.setId(trxTournament1.getId());
        assertThat(trxTournament1).isEqualTo(trxTournament2);

        trxTournament2 = getTrxTournamentSample2();
        assertThat(trxTournament1).isNotEqualTo(trxTournament2);
    }

    @Test
    void eventTest() {
        TrxTournament trxTournament = getTrxTournamentRandomSampleGenerator();
        TrxEvent trxEventBack = getTrxEventRandomSampleGenerator();

        trxTournament.setEvent(trxEventBack);
        assertThat(trxTournament.getEvent()).isEqualTo(trxEventBack);

        trxTournament.event(null);
        assertThat(trxTournament.getEvent()).isNull();
    }
}
