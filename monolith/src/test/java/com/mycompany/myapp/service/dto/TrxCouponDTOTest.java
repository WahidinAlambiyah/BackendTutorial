package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxCouponDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxCouponDTO.class);
        TrxCouponDTO trxCouponDTO1 = new TrxCouponDTO();
        trxCouponDTO1.setId(1L);
        TrxCouponDTO trxCouponDTO2 = new TrxCouponDTO();
        assertThat(trxCouponDTO1).isNotEqualTo(trxCouponDTO2);
        trxCouponDTO2.setId(trxCouponDTO1.getId());
        assertThat(trxCouponDTO1).isEqualTo(trxCouponDTO2);
        trxCouponDTO2.setId(2L);
        assertThat(trxCouponDTO1).isNotEqualTo(trxCouponDTO2);
        trxCouponDTO1.setId(null);
        assertThat(trxCouponDTO1).isNotEqualTo(trxCouponDTO2);
    }
}
