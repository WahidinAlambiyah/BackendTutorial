package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TrxCouponTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxCouponTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxCoupon.class);
        TrxCoupon trxCoupon1 = getTrxCouponSample1();
        TrxCoupon trxCoupon2 = new TrxCoupon();
        assertThat(trxCoupon1).isNotEqualTo(trxCoupon2);

        trxCoupon2.setId(trxCoupon1.getId());
        assertThat(trxCoupon1).isEqualTo(trxCoupon2);

        trxCoupon2 = getTrxCouponSample2();
        assertThat(trxCoupon1).isNotEqualTo(trxCoupon2);
    }
}
