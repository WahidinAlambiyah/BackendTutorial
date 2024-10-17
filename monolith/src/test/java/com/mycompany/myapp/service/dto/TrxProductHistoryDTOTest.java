package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxProductHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxProductHistoryDTO.class);
        TrxProductHistoryDTO trxProductHistoryDTO1 = new TrxProductHistoryDTO();
        trxProductHistoryDTO1.setId(1L);
        TrxProductHistoryDTO trxProductHistoryDTO2 = new TrxProductHistoryDTO();
        assertThat(trxProductHistoryDTO1).isNotEqualTo(trxProductHistoryDTO2);
        trxProductHistoryDTO2.setId(trxProductHistoryDTO1.getId());
        assertThat(trxProductHistoryDTO1).isEqualTo(trxProductHistoryDTO2);
        trxProductHistoryDTO2.setId(2L);
        assertThat(trxProductHistoryDTO1).isNotEqualTo(trxProductHistoryDTO2);
        trxProductHistoryDTO1.setId(null);
        assertThat(trxProductHistoryDTO1).isNotEqualTo(trxProductHistoryDTO2);
    }
}
