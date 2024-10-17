package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxOrderItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxOrderItemDTO.class);
        TrxOrderItemDTO trxOrderItemDTO1 = new TrxOrderItemDTO();
        trxOrderItemDTO1.setId(1L);
        TrxOrderItemDTO trxOrderItemDTO2 = new TrxOrderItemDTO();
        assertThat(trxOrderItemDTO1).isNotEqualTo(trxOrderItemDTO2);
        trxOrderItemDTO2.setId(trxOrderItemDTO1.getId());
        assertThat(trxOrderItemDTO1).isEqualTo(trxOrderItemDTO2);
        trxOrderItemDTO2.setId(2L);
        assertThat(trxOrderItemDTO1).isNotEqualTo(trxOrderItemDTO2);
        trxOrderItemDTO1.setId(null);
        assertThat(trxOrderItemDTO1).isNotEqualTo(trxOrderItemDTO2);
    }
}
