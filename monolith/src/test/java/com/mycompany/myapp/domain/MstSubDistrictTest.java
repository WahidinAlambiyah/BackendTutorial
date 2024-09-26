package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstDistrictTestSamples.*;
import static com.mycompany.myapp.domain.MstPostalCodeTestSamples.*;
import static com.mycompany.myapp.domain.MstSubDistrictTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstSubDistrictTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstSubDistrict.class);
        MstSubDistrict mstSubDistrict1 = getMstSubDistrictSample1();
        MstSubDistrict mstSubDistrict2 = new MstSubDistrict();
        assertThat(mstSubDistrict1).isNotEqualTo(mstSubDistrict2);

        mstSubDistrict2.setId(mstSubDistrict1.getId());
        assertThat(mstSubDistrict1).isEqualTo(mstSubDistrict2);

        mstSubDistrict2 = getMstSubDistrictSample2();
        assertThat(mstSubDistrict1).isNotEqualTo(mstSubDistrict2);
    }

    @Test
    void postalCodeTest() {
        MstSubDistrict mstSubDistrict = getMstSubDistrictRandomSampleGenerator();
        MstPostalCode mstPostalCodeBack = getMstPostalCodeRandomSampleGenerator();

        mstSubDistrict.addPostalCode(mstPostalCodeBack);
        assertThat(mstSubDistrict.getPostalCodes()).containsOnly(mstPostalCodeBack);
        assertThat(mstPostalCodeBack.getSubDistrict()).isEqualTo(mstSubDistrict);

        mstSubDistrict.removePostalCode(mstPostalCodeBack);
        assertThat(mstSubDistrict.getPostalCodes()).doesNotContain(mstPostalCodeBack);
        assertThat(mstPostalCodeBack.getSubDistrict()).isNull();

        mstSubDistrict.postalCodes(new HashSet<>(Set.of(mstPostalCodeBack)));
        assertThat(mstSubDistrict.getPostalCodes()).containsOnly(mstPostalCodeBack);
        assertThat(mstPostalCodeBack.getSubDistrict()).isEqualTo(mstSubDistrict);

        mstSubDistrict.setPostalCodes(new HashSet<>());
        assertThat(mstSubDistrict.getPostalCodes()).doesNotContain(mstPostalCodeBack);
        assertThat(mstPostalCodeBack.getSubDistrict()).isNull();
    }

    @Test
    void districtTest() {
        MstSubDistrict mstSubDistrict = getMstSubDistrictRandomSampleGenerator();
        MstDistrict mstDistrictBack = getMstDistrictRandomSampleGenerator();

        mstSubDistrict.setDistrict(mstDistrictBack);
        assertThat(mstSubDistrict.getDistrict()).isEqualTo(mstDistrictBack);

        mstSubDistrict.district(null);
        assertThat(mstSubDistrict.getDistrict()).isNull();
    }
}
