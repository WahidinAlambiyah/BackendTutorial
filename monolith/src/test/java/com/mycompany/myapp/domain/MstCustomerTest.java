package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCustomerTestSamples.*;
import static com.mycompany.myapp.domain.TrxOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstCustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstCustomer.class);
        MstCustomer mstCustomer1 = getMstCustomerSample1();
        MstCustomer mstCustomer2 = new MstCustomer();
        assertThat(mstCustomer1).isNotEqualTo(mstCustomer2);

        mstCustomer2.setId(mstCustomer1.getId());
        assertThat(mstCustomer1).isEqualTo(mstCustomer2);

        mstCustomer2 = getMstCustomerSample2();
        assertThat(mstCustomer1).isNotEqualTo(mstCustomer2);
    }

    @Test
    void orderTest() {
        MstCustomer mstCustomer = getMstCustomerRandomSampleGenerator();
        TrxOrder trxOrderBack = getTrxOrderRandomSampleGenerator();

        mstCustomer.addOrder(trxOrderBack);
        assertThat(mstCustomer.getOrders()).containsOnly(trxOrderBack);
        assertThat(trxOrderBack.getMstCustomer()).isEqualTo(mstCustomer);

        mstCustomer.removeOrder(trxOrderBack);
        assertThat(mstCustomer.getOrders()).doesNotContain(trxOrderBack);
        assertThat(trxOrderBack.getMstCustomer()).isNull();

        mstCustomer.orders(new HashSet<>(Set.of(trxOrderBack)));
        assertThat(mstCustomer.getOrders()).containsOnly(trxOrderBack);
        assertThat(trxOrderBack.getMstCustomer()).isEqualTo(mstCustomer);

        mstCustomer.setOrders(new HashSet<>());
        assertThat(mstCustomer.getOrders()).doesNotContain(trxOrderBack);
        assertThat(trxOrderBack.getMstCustomer()).isNull();
    }
}
