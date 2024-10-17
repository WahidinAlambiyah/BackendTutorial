package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxCartDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxCartDTO.class);
        TrxCartDTO trxCartDTO1 = new TrxCartDTO();
        trxCartDTO1.setId(1L);
        TrxCartDTO trxCartDTO2 = new TrxCartDTO();
        assertThat(trxCartDTO1).isNotEqualTo(trxCartDTO2);
        trxCartDTO2.setId(trxCartDTO1.getId());
        assertThat(trxCartDTO1).isEqualTo(trxCartDTO2);
        trxCartDTO2.setId(2L);
        assertThat(trxCartDTO1).isNotEqualTo(trxCartDTO2);
        trxCartDTO1.setId(null);
        assertThat(trxCartDTO1).isNotEqualTo(trxCartDTO2);
    }
}
