package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxOrderHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxOrderHistoryDTO.class);
        TrxOrderHistoryDTO trxOrderHistoryDTO1 = new TrxOrderHistoryDTO();
        trxOrderHistoryDTO1.setId(1L);
        TrxOrderHistoryDTO trxOrderHistoryDTO2 = new TrxOrderHistoryDTO();
        assertThat(trxOrderHistoryDTO1).isNotEqualTo(trxOrderHistoryDTO2);
        trxOrderHistoryDTO2.setId(trxOrderHistoryDTO1.getId());
        assertThat(trxOrderHistoryDTO1).isEqualTo(trxOrderHistoryDTO2);
        trxOrderHistoryDTO2.setId(2L);
        assertThat(trxOrderHistoryDTO1).isNotEqualTo(trxOrderHistoryDTO2);
        trxOrderHistoryDTO1.setId(null);
        assertThat(trxOrderHistoryDTO1).isNotEqualTo(trxOrderHistoryDTO2);
    }
}
