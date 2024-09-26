package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstPostalCodeTestSamples.*;
import static com.mycompany.myapp.domain.MstSubDistrictTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstPostalCodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstPostalCode.class);
        MstPostalCode mstPostalCode1 = getMstPostalCodeSample1();
        MstPostalCode mstPostalCode2 = new MstPostalCode();
        assertThat(mstPostalCode1).isNotEqualTo(mstPostalCode2);

        mstPostalCode2.setId(mstPostalCode1.getId());
        assertThat(mstPostalCode1).isEqualTo(mstPostalCode2);

        mstPostalCode2 = getMstPostalCodeSample2();
        assertThat(mstPostalCode1).isNotEqualTo(mstPostalCode2);
    }

    @Test
    void subDistrictTest() {
        MstPostalCode mstPostalCode = getMstPostalCodeRandomSampleGenerator();
        MstSubDistrict mstSubDistrictBack = getMstSubDistrictRandomSampleGenerator();

        mstPostalCode.setSubDistrict(mstSubDistrictBack);
        assertThat(mstPostalCode.getSubDistrict()).isEqualTo(mstSubDistrictBack);

        mstPostalCode.subDistrict(null);
        assertThat(mstPostalCode.getSubDistrict()).isNull();
    }
}
