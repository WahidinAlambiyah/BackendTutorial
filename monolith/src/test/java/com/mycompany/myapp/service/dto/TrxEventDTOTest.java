package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxEventDTO.class);
        TrxEventDTO trxEventDTO1 = new TrxEventDTO();
        trxEventDTO1.setId(1L);
        TrxEventDTO trxEventDTO2 = new TrxEventDTO();
        assertThat(trxEventDTO1).isNotEqualTo(trxEventDTO2);
        trxEventDTO2.setId(trxEventDTO1.getId());
        assertThat(trxEventDTO1).isEqualTo(trxEventDTO2);
        trxEventDTO2.setId(2L);
        assertThat(trxEventDTO1).isNotEqualTo(trxEventDTO2);
        trxEventDTO1.setId(null);
        assertThat(trxEventDTO1).isNotEqualTo(trxEventDTO2);
    }
}
