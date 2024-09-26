package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxTournamentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxTournamentDTO.class);
        TrxTournamentDTO trxTournamentDTO1 = new TrxTournamentDTO();
        trxTournamentDTO1.setId(1L);
        TrxTournamentDTO trxTournamentDTO2 = new TrxTournamentDTO();
        assertThat(trxTournamentDTO1).isNotEqualTo(trxTournamentDTO2);
        trxTournamentDTO2.setId(trxTournamentDTO1.getId());
        assertThat(trxTournamentDTO1).isEqualTo(trxTournamentDTO2);
        trxTournamentDTO2.setId(2L);
        assertThat(trxTournamentDTO1).isNotEqualTo(trxTournamentDTO2);
        trxTournamentDTO1.setId(null);
        assertThat(trxTournamentDTO1).isNotEqualTo(trxTournamentDTO2);
    }
}
