package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxDiscountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxDiscountDTO.class);
        TrxDiscountDTO trxDiscountDTO1 = new TrxDiscountDTO();
        trxDiscountDTO1.setId(1L);
        TrxDiscountDTO trxDiscountDTO2 = new TrxDiscountDTO();
        assertThat(trxDiscountDTO1).isNotEqualTo(trxDiscountDTO2);
        trxDiscountDTO2.setId(trxDiscountDTO1.getId());
        assertThat(trxDiscountDTO1).isEqualTo(trxDiscountDTO2);
        trxDiscountDTO2.setId(2L);
        assertThat(trxDiscountDTO1).isNotEqualTo(trxDiscountDTO2);
        trxDiscountDTO1.setId(null);
        assertThat(trxDiscountDTO1).isNotEqualTo(trxDiscountDTO2);
    }
}
