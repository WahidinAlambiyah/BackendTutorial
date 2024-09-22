package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CityTestSamples.*;
import static com.mycompany.myapp.domain.DistrictTestSamples.*;
import static com.mycompany.myapp.domain.SubDistrictTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DistrictTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(District.class);
        District district1 = getDistrictSample1();
        District district2 = new District();
        assertThat(district1).isNotEqualTo(district2);

        district2.setId(district1.getId());
        assertThat(district1).isEqualTo(district2);

        district2 = getDistrictSample2();
        assertThat(district1).isNotEqualTo(district2);
    }

    @Test
    void subDistrictTest() {
        District district = getDistrictRandomSampleGenerator();
        SubDistrict subDistrictBack = getSubDistrictRandomSampleGenerator();

        district.addSubDistrict(subDistrictBack);
        assertThat(district.getSubDistricts()).containsOnly(subDistrictBack);
        assertThat(subDistrictBack.getDistrict()).isEqualTo(district);

        district.removeSubDistrict(subDistrictBack);
        assertThat(district.getSubDistricts()).doesNotContain(subDistrictBack);
        assertThat(subDistrictBack.getDistrict()).isNull();

        district.subDistricts(new HashSet<>(Set.of(subDistrictBack)));
        assertThat(district.getSubDistricts()).containsOnly(subDistrictBack);
        assertThat(subDistrictBack.getDistrict()).isEqualTo(district);

        district.setSubDistricts(new HashSet<>());
        assertThat(district.getSubDistricts()).doesNotContain(subDistrictBack);
        assertThat(subDistrictBack.getDistrict()).isNull();
    }

    @Test
    void cityTest() {
        District district = getDistrictRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        district.setCity(cityBack);
        assertThat(district.getCity()).isEqualTo(cityBack);

        district.city(null);
        assertThat(district.getCity()).isNull();
    }
}
