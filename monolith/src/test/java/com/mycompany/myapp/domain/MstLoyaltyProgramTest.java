package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCustomerTestSamples.*;
import static com.mycompany.myapp.domain.MstLoyaltyProgramTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstLoyaltyProgramTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstLoyaltyProgram.class);
        MstLoyaltyProgram mstLoyaltyProgram1 = getMstLoyaltyProgramSample1();
        MstLoyaltyProgram mstLoyaltyProgram2 = new MstLoyaltyProgram();
        assertThat(mstLoyaltyProgram1).isNotEqualTo(mstLoyaltyProgram2);

        mstLoyaltyProgram2.setId(mstLoyaltyProgram1.getId());
        assertThat(mstLoyaltyProgram1).isEqualTo(mstLoyaltyProgram2);

        mstLoyaltyProgram2 = getMstLoyaltyProgramSample2();
        assertThat(mstLoyaltyProgram1).isNotEqualTo(mstLoyaltyProgram2);
    }

    @Test
    void customerTest() {
        MstLoyaltyProgram mstLoyaltyProgram = getMstLoyaltyProgramRandomSampleGenerator();
        MstCustomer mstCustomerBack = getMstCustomerRandomSampleGenerator();

        mstLoyaltyProgram.setCustomer(mstCustomerBack);
        assertThat(mstLoyaltyProgram.getCustomer()).isEqualTo(mstCustomerBack);

        mstLoyaltyProgram.customer(null);
        assertThat(mstLoyaltyProgram.getCustomer()).isNull();
    }
}
