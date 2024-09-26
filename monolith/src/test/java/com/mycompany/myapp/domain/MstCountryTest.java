package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCountryTestSamples.*;
import static com.mycompany.myapp.domain.MstProvinceTestSamples.*;
import static com.mycompany.myapp.domain.MstRegionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstCountryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstCountry.class);
        MstCountry mstCountry1 = getMstCountrySample1();
        MstCountry mstCountry2 = new MstCountry();
        assertThat(mstCountry1).isNotEqualTo(mstCountry2);

        mstCountry2.setId(mstCountry1.getId());
        assertThat(mstCountry1).isEqualTo(mstCountry2);

        mstCountry2 = getMstCountrySample2();
        assertThat(mstCountry1).isNotEqualTo(mstCountry2);
    }

    @Test
    void provinceTest() {
        MstCountry mstCountry = getMstCountryRandomSampleGenerator();
        MstProvince mstProvinceBack = getMstProvinceRandomSampleGenerator();

        mstCountry.addProvince(mstProvinceBack);
        assertThat(mstCountry.getProvinces()).containsOnly(mstProvinceBack);
        assertThat(mstProvinceBack.getCountry()).isEqualTo(mstCountry);

        mstCountry.removeProvince(mstProvinceBack);
        assertThat(mstCountry.getProvinces()).doesNotContain(mstProvinceBack);
        assertThat(mstProvinceBack.getCountry()).isNull();

        mstCountry.provinces(new HashSet<>(Set.of(mstProvinceBack)));
        assertThat(mstCountry.getProvinces()).containsOnly(mstProvinceBack);
        assertThat(mstProvinceBack.getCountry()).isEqualTo(mstCountry);

        mstCountry.setProvinces(new HashSet<>());
        assertThat(mstCountry.getProvinces()).doesNotContain(mstProvinceBack);
        assertThat(mstProvinceBack.getCountry()).isNull();
    }

    @Test
    void regionTest() {
        MstCountry mstCountry = getMstCountryRandomSampleGenerator();
        MstRegion mstRegionBack = getMstRegionRandomSampleGenerator();

        mstCountry.setRegion(mstRegionBack);
        assertThat(mstCountry.getRegion()).isEqualTo(mstRegionBack);

        mstCountry.region(null);
        assertThat(mstCountry.getRegion()).isNull();
    }
}
