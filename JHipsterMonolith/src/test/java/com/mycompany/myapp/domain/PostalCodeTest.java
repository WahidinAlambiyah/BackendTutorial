package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.PostalCodeTestSamples.*;
import static com.mycompany.myapp.domain.SubDistrictTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostalCodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostalCode.class);
        PostalCode postalCode1 = getPostalCodeSample1();
        PostalCode postalCode2 = new PostalCode();
        assertThat(postalCode1).isNotEqualTo(postalCode2);

        postalCode2.setId(postalCode1.getId());
        assertThat(postalCode1).isEqualTo(postalCode2);

        postalCode2 = getPostalCodeSample2();
        assertThat(postalCode1).isNotEqualTo(postalCode2);
    }

    @Test
    void subDistrictTest() {
        PostalCode postalCode = getPostalCodeRandomSampleGenerator();
        SubDistrict subDistrictBack = getSubDistrictRandomSampleGenerator();

        postalCode.setSubDistrict(subDistrictBack);
        assertThat(postalCode.getSubDistrict()).isEqualTo(subDistrictBack);

        postalCode.subDistrict(null);
        assertThat(postalCode.getSubDistrict()).isNull();
    }
}
