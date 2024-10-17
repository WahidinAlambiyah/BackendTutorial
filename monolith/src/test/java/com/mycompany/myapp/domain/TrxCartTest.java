package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCustomerTestSamples.*;
import static com.mycompany.myapp.domain.TrxCartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxCartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxCart.class);
        TrxCart trxCart1 = getTrxCartSample1();
        TrxCart trxCart2 = new TrxCart();
        assertThat(trxCart1).isNotEqualTo(trxCart2);

        trxCart2.setId(trxCart1.getId());
        assertThat(trxCart1).isEqualTo(trxCart2);

        trxCart2 = getTrxCartSample2();
        assertThat(trxCart1).isNotEqualTo(trxCart2);
    }

    @Test
    void customerTest() {
        TrxCart trxCart = getTrxCartRandomSampleGenerator();
        MstCustomer mstCustomerBack = getMstCustomerRandomSampleGenerator();

        trxCart.setCustomer(mstCustomerBack);
        assertThat(trxCart.getCustomer()).isEqualTo(mstCustomerBack);

        trxCart.customer(null);
        assertThat(trxCart.getCustomer()).isNull();
    }
}
