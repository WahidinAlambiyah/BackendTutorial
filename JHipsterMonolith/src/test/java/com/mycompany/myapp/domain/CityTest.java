package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CityTestSamples.*;
import static com.mycompany.myapp.domain.DistrictTestSamples.*;
import static com.mycompany.myapp.domain.ProvinceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(City.class);
        City city1 = getCitySample1();
        City city2 = new City();
        assertThat(city1).isNotEqualTo(city2);

        city2.setId(city1.getId());
        assertThat(city1).isEqualTo(city2);

        city2 = getCitySample2();
        assertThat(city1).isNotEqualTo(city2);
    }

    @Test
    void districtTest() {
        City city = getCityRandomSampleGenerator();
        District districtBack = getDistrictRandomSampleGenerator();

        city.addDistrict(districtBack);
        assertThat(city.getDistricts()).containsOnly(districtBack);
        assertThat(districtBack.getCity()).isEqualTo(city);

        city.removeDistrict(districtBack);
        assertThat(city.getDistricts()).doesNotContain(districtBack);
        assertThat(districtBack.getCity()).isNull();

        city.districts(new HashSet<>(Set.of(districtBack)));
        assertThat(city.getDistricts()).containsOnly(districtBack);
        assertThat(districtBack.getCity()).isEqualTo(city);

        city.setDistricts(new HashSet<>());
        assertThat(city.getDistricts()).doesNotContain(districtBack);
        assertThat(districtBack.getCity()).isNull();
    }

    @Test
    void provinceTest() {
        City city = getCityRandomSampleGenerator();
        Province provinceBack = getProvinceRandomSampleGenerator();

        city.setProvince(provinceBack);
        assertThat(city.getProvince()).isEqualTo(provinceBack);

        city.province(null);
        assertThat(city.getProvince()).isNull();
    }
}
