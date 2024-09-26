package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCityTestSamples.*;
import static com.mycompany.myapp.domain.MstCountryTestSamples.*;
import static com.mycompany.myapp.domain.MstProvinceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstProvinceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstProvince.class);
        MstProvince mstProvince1 = getMstProvinceSample1();
        MstProvince mstProvince2 = new MstProvince();
        assertThat(mstProvince1).isNotEqualTo(mstProvince2);

        mstProvince2.setId(mstProvince1.getId());
        assertThat(mstProvince1).isEqualTo(mstProvince2);

        mstProvince2 = getMstProvinceSample2();
        assertThat(mstProvince1).isNotEqualTo(mstProvince2);
    }

    @Test
    void cityTest() {
        MstProvince mstProvince = getMstProvinceRandomSampleGenerator();
        MstCity mstCityBack = getMstCityRandomSampleGenerator();

        mstProvince.addCity(mstCityBack);
        assertThat(mstProvince.getCities()).containsOnly(mstCityBack);
        assertThat(mstCityBack.getProvince()).isEqualTo(mstProvince);

        mstProvince.removeCity(mstCityBack);
        assertThat(mstProvince.getCities()).doesNotContain(mstCityBack);
        assertThat(mstCityBack.getProvince()).isNull();

        mstProvince.cities(new HashSet<>(Set.of(mstCityBack)));
        assertThat(mstProvince.getCities()).containsOnly(mstCityBack);
        assertThat(mstCityBack.getProvince()).isEqualTo(mstProvince);

        mstProvince.setCities(new HashSet<>());
        assertThat(mstProvince.getCities()).doesNotContain(mstCityBack);
        assertThat(mstCityBack.getProvince()).isNull();
    }

    @Test
    void countryTest() {
        MstProvince mstProvince = getMstProvinceRandomSampleGenerator();
        MstCountry mstCountryBack = getMstCountryRandomSampleGenerator();

        mstProvince.setCountry(mstCountryBack);
        assertThat(mstProvince.getCountry()).isEqualTo(mstCountryBack);

        mstProvince.country(null);
        assertThat(mstProvince.getCountry()).isNull();
    }
}
