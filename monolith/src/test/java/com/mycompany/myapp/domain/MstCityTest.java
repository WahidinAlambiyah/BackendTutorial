package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCityTestSamples.*;
import static com.mycompany.myapp.domain.MstDistrictTestSamples.*;
import static com.mycompany.myapp.domain.MstProvinceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstCityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstCity.class);
        MstCity mstCity1 = getMstCitySample1();
        MstCity mstCity2 = new MstCity();
        assertThat(mstCity1).isNotEqualTo(mstCity2);

        mstCity2.setId(mstCity1.getId());
        assertThat(mstCity1).isEqualTo(mstCity2);

        mstCity2 = getMstCitySample2();
        assertThat(mstCity1).isNotEqualTo(mstCity2);
    }

    @Test
    void districtTest() {
        MstCity mstCity = getMstCityRandomSampleGenerator();
        MstDistrict mstDistrictBack = getMstDistrictRandomSampleGenerator();

        mstCity.addDistrict(mstDistrictBack);
        assertThat(mstCity.getDistricts()).containsOnly(mstDistrictBack);
        assertThat(mstDistrictBack.getCity()).isEqualTo(mstCity);

        mstCity.removeDistrict(mstDistrictBack);
        assertThat(mstCity.getDistricts()).doesNotContain(mstDistrictBack);
        assertThat(mstDistrictBack.getCity()).isNull();

        mstCity.districts(new HashSet<>(Set.of(mstDistrictBack)));
        assertThat(mstCity.getDistricts()).containsOnly(mstDistrictBack);
        assertThat(mstDistrictBack.getCity()).isEqualTo(mstCity);

        mstCity.setDistricts(new HashSet<>());
        assertThat(mstCity.getDistricts()).doesNotContain(mstDistrictBack);
        assertThat(mstDistrictBack.getCity()).isNull();
    }

    @Test
    void provinceTest() {
        MstCity mstCity = getMstCityRandomSampleGenerator();
        MstProvince mstProvinceBack = getMstProvinceRandomSampleGenerator();

        mstCity.setProvince(mstProvinceBack);
        assertThat(mstCity.getProvince()).isEqualTo(mstProvinceBack);

        mstCity.province(null);
        assertThat(mstCity.getProvince()).isNull();
    }
}
