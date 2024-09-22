package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CountryTestSamples.*;
import static com.mycompany.myapp.domain.ProvinceTestSamples.*;
import static com.mycompany.myapp.domain.RegionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Country.class);
        Country country1 = getCountrySample1();
        Country country2 = new Country();
        assertThat(country1).isNotEqualTo(country2);

        country2.setId(country1.getId());
        assertThat(country1).isEqualTo(country2);

        country2 = getCountrySample2();
        assertThat(country1).isNotEqualTo(country2);
    }

    @Test
    void provinceTest() {
        Country country = getCountryRandomSampleGenerator();
        Province provinceBack = getProvinceRandomSampleGenerator();

        country.addProvince(provinceBack);
        assertThat(country.getProvinces()).containsOnly(provinceBack);
        assertThat(provinceBack.getCountry()).isEqualTo(country);

        country.removeProvince(provinceBack);
        assertThat(country.getProvinces()).doesNotContain(provinceBack);
        assertThat(provinceBack.getCountry()).isNull();

        country.provinces(new HashSet<>(Set.of(provinceBack)));
        assertThat(country.getProvinces()).containsOnly(provinceBack);
        assertThat(provinceBack.getCountry()).isEqualTo(country);

        country.setProvinces(new HashSet<>());
        assertThat(country.getProvinces()).doesNotContain(provinceBack);
        assertThat(provinceBack.getCountry()).isNull();
    }

    @Test
    void regionTest() {
        Country country = getCountryRandomSampleGenerator();
        Region regionBack = getRegionRandomSampleGenerator();

        country.setRegion(regionBack);
        assertThat(country.getRegion()).isEqualTo(regionBack);

        country.region(null);
        assertThat(country.getRegion()).isNull();
    }
}
