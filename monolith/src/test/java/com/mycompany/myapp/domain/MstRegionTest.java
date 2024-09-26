package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCountryTestSamples.*;
import static com.mycompany.myapp.domain.MstRegionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstRegionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstRegion.class);
        MstRegion mstRegion1 = getMstRegionSample1();
        MstRegion mstRegion2 = new MstRegion();
        assertThat(mstRegion1).isNotEqualTo(mstRegion2);

        mstRegion2.setId(mstRegion1.getId());
        assertThat(mstRegion1).isEqualTo(mstRegion2);

        mstRegion2 = getMstRegionSample2();
        assertThat(mstRegion1).isNotEqualTo(mstRegion2);
    }

    @Test
    void countryTest() {
        MstRegion mstRegion = getMstRegionRandomSampleGenerator();
        MstCountry mstCountryBack = getMstCountryRandomSampleGenerator();

        mstRegion.addCountry(mstCountryBack);
        assertThat(mstRegion.getCountries()).containsOnly(mstCountryBack);
        assertThat(mstCountryBack.getRegion()).isEqualTo(mstRegion);

        mstRegion.removeCountry(mstCountryBack);
        assertThat(mstRegion.getCountries()).doesNotContain(mstCountryBack);
        assertThat(mstCountryBack.getRegion()).isNull();

        mstRegion.countries(new HashSet<>(Set.of(mstCountryBack)));
        assertThat(mstRegion.getCountries()).containsOnly(mstCountryBack);
        assertThat(mstCountryBack.getRegion()).isEqualTo(mstRegion);

        mstRegion.setCountries(new HashSet<>());
        assertThat(mstRegion.getCountries()).doesNotContain(mstCountryBack);
        assertThat(mstCountryBack.getRegion()).isNull();
    }
}
