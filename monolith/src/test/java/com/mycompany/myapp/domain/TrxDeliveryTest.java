package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstDriverTestSamples.*;
import static com.mycompany.myapp.domain.TrxDeliveryTestSamples.*;
import static com.mycompany.myapp.domain.TrxOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxDeliveryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxDelivery.class);
        TrxDelivery trxDelivery1 = getTrxDeliverySample1();
        TrxDelivery trxDelivery2 = new TrxDelivery();
        assertThat(trxDelivery1).isNotEqualTo(trxDelivery2);

        trxDelivery2.setId(trxDelivery1.getId());
        assertThat(trxDelivery1).isEqualTo(trxDelivery2);

        trxDelivery2 = getTrxDeliverySample2();
        assertThat(trxDelivery1).isNotEqualTo(trxDelivery2);
    }

    @Test
    void driverTest() {
        TrxDelivery trxDelivery = getTrxDeliveryRandomSampleGenerator();
        MstDriver mstDriverBack = getMstDriverRandomSampleGenerator();

        trxDelivery.setDriver(mstDriverBack);
        assertThat(trxDelivery.getDriver()).isEqualTo(mstDriverBack);

        trxDelivery.driver(null);
        assertThat(trxDelivery.getDriver()).isNull();
    }

    @Test
    void trxOrderTest() {
        TrxDelivery trxDelivery = getTrxDeliveryRandomSampleGenerator();
        TrxOrder trxOrderBack = getTrxOrderRandomSampleGenerator();

        trxDelivery.setTrxOrder(trxOrderBack);
        assertThat(trxDelivery.getTrxOrder()).isEqualTo(trxOrderBack);

        trxDelivery.trxOrder(null);
        assertThat(trxDelivery.getTrxOrder()).isNull();
    }
}
