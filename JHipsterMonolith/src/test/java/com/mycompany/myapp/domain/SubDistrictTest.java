package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DistrictTestSamples.*;
import static com.mycompany.myapp.domain.PostalCodeTestSamples.*;
import static com.mycompany.myapp.domain.SubDistrictTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SubDistrictTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubDistrict.class);
        SubDistrict subDistrict1 = getSubDistrictSample1();
        SubDistrict subDistrict2 = new SubDistrict();
        assertThat(subDistrict1).isNotEqualTo(subDistrict2);

        subDistrict2.setId(subDistrict1.getId());
        assertThat(subDistrict1).isEqualTo(subDistrict2);

        subDistrict2 = getSubDistrictSample2();
        assertThat(subDistrict1).isNotEqualTo(subDistrict2);
    }

    @Test
    void postalCodeTest() {
        SubDistrict subDistrict = getSubDistrictRandomSampleGenerator();
        PostalCode postalCodeBack = getPostalCodeRandomSampleGenerator();

        subDistrict.addPostalCode(postalCodeBack);
        assertThat(subDistrict.getPostalCodes()).containsOnly(postalCodeBack);
        assertThat(postalCodeBack.getSubDistrict()).isEqualTo(subDistrict);

        subDistrict.removePostalCode(postalCodeBack);
        assertThat(subDistrict.getPostalCodes()).doesNotContain(postalCodeBack);
        assertThat(postalCodeBack.getSubDistrict()).isNull();

        subDistrict.postalCodes(new HashSet<>(Set.of(postalCodeBack)));
        assertThat(subDistrict.getPostalCodes()).containsOnly(postalCodeBack);
        assertThat(postalCodeBack.getSubDistrict()).isEqualTo(subDistrict);

        subDistrict.setPostalCodes(new HashSet<>());
        assertThat(subDistrict.getPostalCodes()).doesNotContain(postalCodeBack);
        assertThat(postalCodeBack.getSubDistrict()).isNull();
    }

    @Test
    void districtTest() {
        SubDistrict subDistrict = getSubDistrictRandomSampleGenerator();
        District districtBack = getDistrictRandomSampleGenerator();

        subDistrict.setDistrict(districtBack);
        assertThat(subDistrict.getDistrict()).isEqualTo(districtBack);

        subDistrict.district(null);
        assertThat(subDistrict.getDistrict()).isNull();
    }
}
