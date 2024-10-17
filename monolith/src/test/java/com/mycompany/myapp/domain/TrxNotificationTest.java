package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCustomerTestSamples.*;
import static com.mycompany.myapp.domain.TrxNotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxNotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxNotification.class);
        TrxNotification trxNotification1 = getTrxNotificationSample1();
        TrxNotification trxNotification2 = new TrxNotification();
        assertThat(trxNotification1).isNotEqualTo(trxNotification2);

        trxNotification2.setId(trxNotification1.getId());
        assertThat(trxNotification1).isEqualTo(trxNotification2);

        trxNotification2 = getTrxNotificationSample2();
        assertThat(trxNotification1).isNotEqualTo(trxNotification2);
    }

    @Test
    void customerTest() {
        TrxNotification trxNotification = getTrxNotificationRandomSampleGenerator();
        MstCustomer mstCustomerBack = getMstCustomerRandomSampleGenerator();

        trxNotification.setCustomer(mstCustomerBack);
        assertThat(trxNotification.getCustomer()).isEqualTo(mstCustomerBack);

        trxNotification.customer(null);
        assertThat(trxNotification.getCustomer()).isNull();
    }
}
