package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstProductTestSamples.*;
import static com.mycompany.myapp.domain.TrxOrderItemTestSamples.*;
import static com.mycompany.myapp.domain.TrxOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxOrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxOrderItem.class);
        TrxOrderItem trxOrderItem1 = getTrxOrderItemSample1();
        TrxOrderItem trxOrderItem2 = new TrxOrderItem();
        assertThat(trxOrderItem1).isNotEqualTo(trxOrderItem2);

        trxOrderItem2.setId(trxOrderItem1.getId());
        assertThat(trxOrderItem1).isEqualTo(trxOrderItem2);

        trxOrderItem2 = getTrxOrderItemSample2();
        assertThat(trxOrderItem1).isNotEqualTo(trxOrderItem2);
    }

    @Test
    void orderTest() {
        TrxOrderItem trxOrderItem = getTrxOrderItemRandomSampleGenerator();
        TrxOrder trxOrderBack = getTrxOrderRandomSampleGenerator();

        trxOrderItem.setOrder(trxOrderBack);
        assertThat(trxOrderItem.getOrder()).isEqualTo(trxOrderBack);

        trxOrderItem.order(null);
        assertThat(trxOrderItem.getOrder()).isNull();
    }

    @Test
    void productTest() {
        TrxOrderItem trxOrderItem = getTrxOrderItemRandomSampleGenerator();
        MstProduct mstProductBack = getMstProductRandomSampleGenerator();

        trxOrderItem.setProduct(mstProductBack);
        assertThat(trxOrderItem.getProduct()).isEqualTo(mstProductBack);

        trxOrderItem.product(null);
        assertThat(trxOrderItem.getProduct()).isNull();
    }
}
