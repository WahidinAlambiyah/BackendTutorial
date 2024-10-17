package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxStockAlertDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxStockAlertDTO.class);
        TrxStockAlertDTO trxStockAlertDTO1 = new TrxStockAlertDTO();
        trxStockAlertDTO1.setId(1L);
        TrxStockAlertDTO trxStockAlertDTO2 = new TrxStockAlertDTO();
        assertThat(trxStockAlertDTO1).isNotEqualTo(trxStockAlertDTO2);
        trxStockAlertDTO2.setId(trxStockAlertDTO1.getId());
        assertThat(trxStockAlertDTO1).isEqualTo(trxStockAlertDTO2);
        trxStockAlertDTO2.setId(2L);
        assertThat(trxStockAlertDTO1).isNotEqualTo(trxStockAlertDTO2);
        trxStockAlertDTO1.setId(null);
        assertThat(trxStockAlertDTO1).isNotEqualTo(trxStockAlertDTO2);
    }
}
