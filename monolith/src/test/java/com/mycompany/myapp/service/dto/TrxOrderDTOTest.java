package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxOrderDTO.class);
        TrxOrderDTO trxOrderDTO1 = new TrxOrderDTO();
        trxOrderDTO1.setId(1L);
        TrxOrderDTO trxOrderDTO2 = new TrxOrderDTO();
        assertThat(trxOrderDTO1).isNotEqualTo(trxOrderDTO2);
        trxOrderDTO2.setId(trxOrderDTO1.getId());
        assertThat(trxOrderDTO1).isEqualTo(trxOrderDTO2);
        trxOrderDTO2.setId(2L);
        assertThat(trxOrderDTO1).isNotEqualTo(trxOrderDTO2);
        trxOrderDTO1.setId(null);
        assertThat(trxOrderDTO1).isNotEqualTo(trxOrderDTO2);
    }
}
