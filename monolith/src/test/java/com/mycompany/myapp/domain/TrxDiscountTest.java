package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TrxDiscountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxDiscountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxDiscount.class);
        TrxDiscount trxDiscount1 = getTrxDiscountSample1();
        TrxDiscount trxDiscount2 = new TrxDiscount();
        assertThat(trxDiscount1).isNotEqualTo(trxDiscount2);

        trxDiscount2.setId(trxDiscount1.getId());
        assertThat(trxDiscount1).isEqualTo(trxDiscount2);

        trxDiscount2 = getTrxDiscountSample2();
        assertThat(trxDiscount1).isNotEqualTo(trxDiscount2);
    }
}
