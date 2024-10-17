package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxOrderStockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxOrderStockDTO.class);
        TrxOrderStockDTO trxOrderStockDTO1 = new TrxOrderStockDTO();
        trxOrderStockDTO1.setId(1L);
        TrxOrderStockDTO trxOrderStockDTO2 = new TrxOrderStockDTO();
        assertThat(trxOrderStockDTO1).isNotEqualTo(trxOrderStockDTO2);
        trxOrderStockDTO2.setId(trxOrderStockDTO1.getId());
        assertThat(trxOrderStockDTO1).isEqualTo(trxOrderStockDTO2);
        trxOrderStockDTO2.setId(2L);
        assertThat(trxOrderStockDTO1).isNotEqualTo(trxOrderStockDTO2);
        trxOrderStockDTO1.setId(null);
        assertThat(trxOrderStockDTO1).isNotEqualTo(trxOrderStockDTO2);
    }
}
