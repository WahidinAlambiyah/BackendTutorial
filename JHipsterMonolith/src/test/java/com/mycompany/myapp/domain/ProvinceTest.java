package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CityTestSamples.*;
import static com.mycompany.myapp.domain.CountryTestSamples.*;
import static com.mycompany.myapp.domain.ProvinceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProvinceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Province.class);
        Province province1 = getProvinceSample1();
        Province province2 = new Province();
        assertThat(province1).isNotEqualTo(province2);

        province2.setId(province1.getId());
        assertThat(province1).isEqualTo(province2);

        province2 = getProvinceSample2();
        assertThat(province1).isNotEqualTo(province2);
    }

    @Test
    void cityTest() {
        Province province = getProvinceRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        province.addCity(cityBack);
        assertThat(province.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getProvince()).isEqualTo(province);

        province.removeCity(cityBack);
        assertThat(province.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getProvince()).isNull();

        province.cities(new HashSet<>(Set.of(cityBack)));
        assertThat(province.getCities()).containsOnly(cityBack);
        assertThat(cityBack.getProvince()).isEqualTo(province);

        province.setCities(new HashSet<>());
        assertThat(province.getCities()).doesNotContain(cityBack);
        assertThat(cityBack.getProvince()).isNull();
    }

    @Test
    void countryTest() {
        Province province = getProvinceRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        province.setCountry(countryBack);
        assertThat(province.getCountry()).isEqualTo(countryBack);

        province.country(null);
        assertThat(province.getCountry()).isNull();
    }
}
