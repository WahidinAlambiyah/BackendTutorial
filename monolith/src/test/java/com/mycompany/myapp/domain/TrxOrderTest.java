package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCustomerTestSamples.*;
import static com.mycompany.myapp.domain.TrxDeliveryTestSamples.*;
import static com.mycompany.myapp.domain.TrxOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TrxOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxOrder.class);
        TrxOrder trxOrder1 = getTrxOrderSample1();
        TrxOrder trxOrder2 = new TrxOrder();
        assertThat(trxOrder1).isNotEqualTo(trxOrder2);

        trxOrder2.setId(trxOrder1.getId());
        assertThat(trxOrder1).isEqualTo(trxOrder2);

        trxOrder2 = getTrxOrderSample2();
        assertThat(trxOrder1).isNotEqualTo(trxOrder2);
    }

    @Test
    void deliveryTest() {
        TrxOrder trxOrder = getTrxOrderRandomSampleGenerator();
        TrxDelivery trxDeliveryBack = getTrxDeliveryRandomSampleGenerator();

        trxOrder.addDelivery(trxDeliveryBack);
        assertThat(trxOrder.getDeliveries()).containsOnly(trxDeliveryBack);
        assertThat(trxDeliveryBack.getTrxOrder()).isEqualTo(trxOrder);

        trxOrder.removeDelivery(trxDeliveryBack);
        assertThat(trxOrder.getDeliveries()).doesNotContain(trxDeliveryBack);
        assertThat(trxDeliveryBack.getTrxOrder()).isNull();

        trxOrder.deliveries(new HashSet<>(Set.of(trxDeliveryBack)));
        assertThat(trxOrder.getDeliveries()).containsOnly(trxDeliveryBack);
        assertThat(trxDeliveryBack.getTrxOrder()).isEqualTo(trxOrder);

        trxOrder.setDeliveries(new HashSet<>());
        assertThat(trxOrder.getDeliveries()).doesNotContain(trxDeliveryBack);
        assertThat(trxDeliveryBack.getTrxOrder()).isNull();
    }

    @Test
    void mstCustomerTest() {
        TrxOrder trxOrder = getTrxOrderRandomSampleGenerator();
        MstCustomer mstCustomerBack = getMstCustomerRandomSampleGenerator();

        trxOrder.setMstCustomer(mstCustomerBack);
        assertThat(trxOrder.getMstCustomer()).isEqualTo(mstCustomerBack);

        trxOrder.mstCustomer(null);
        assertThat(trxOrder.getMstCustomer()).isNull();
    }
}
