package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCityTestSamples.*;
import static com.mycompany.myapp.domain.MstDistrictTestSamples.*;
import static com.mycompany.myapp.domain.MstSubDistrictTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstDistrictTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstDistrict.class);
        MstDistrict mstDistrict1 = getMstDistrictSample1();
        MstDistrict mstDistrict2 = new MstDistrict();
        assertThat(mstDistrict1).isNotEqualTo(mstDistrict2);

        mstDistrict2.setId(mstDistrict1.getId());
        assertThat(mstDistrict1).isEqualTo(mstDistrict2);

        mstDistrict2 = getMstDistrictSample2();
        assertThat(mstDistrict1).isNotEqualTo(mstDistrict2);
    }

    @Test
    void subDistrictTest() {
        MstDistrict mstDistrict = getMstDistrictRandomSampleGenerator();
        MstSubDistrict mstSubDistrictBack = getMstSubDistrictRandomSampleGenerator();

        mstDistrict.addSubDistrict(mstSubDistrictBack);
        assertThat(mstDistrict.getSubDistricts()).containsOnly(mstSubDistrictBack);
        assertThat(mstSubDistrictBack.getDistrict()).isEqualTo(mstDistrict);

        mstDistrict.removeSubDistrict(mstSubDistrictBack);
        assertThat(mstDistrict.getSubDistricts()).doesNotContain(mstSubDistrictBack);
        assertThat(mstSubDistrictBack.getDistrict()).isNull();

        mstDistrict.subDistricts(new HashSet<>(Set.of(mstSubDistrictBack)));
        assertThat(mstDistrict.getSubDistricts()).containsOnly(mstSubDistrictBack);
        assertThat(mstSubDistrictBack.getDistrict()).isEqualTo(mstDistrict);

        mstDistrict.setSubDistricts(new HashSet<>());
        assertThat(mstDistrict.getSubDistricts()).doesNotContain(mstSubDistrictBack);
        assertThat(mstSubDistrictBack.getDistrict()).isNull();
    }

    @Test
    void cityTest() {
        MstDistrict mstDistrict = getMstDistrictRandomSampleGenerator();
        MstCity mstCityBack = getMstCityRandomSampleGenerator();

        mstDistrict.setCity(mstCityBack);
        assertThat(mstDistrict.getCity()).isEqualTo(mstCityBack);

        mstDistrict.city(null);
        assertThat(mstDistrict.getCity()).isNull();
    }
}
