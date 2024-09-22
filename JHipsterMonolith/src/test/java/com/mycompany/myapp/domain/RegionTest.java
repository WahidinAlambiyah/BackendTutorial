package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CountryTestSamples.*;
import static com.mycompany.myapp.domain.RegionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RegionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Region.class);
        Region region1 = getRegionSample1();
        Region region2 = new Region();
        assertThat(region1).isNotEqualTo(region2);

        region2.setId(region1.getId());
        assertThat(region1).isEqualTo(region2);

        region2 = getRegionSample2();
        assertThat(region1).isNotEqualTo(region2);
    }

    @Test
    void countryTest() {
        Region region = getRegionRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        region.addCountry(countryBack);
        assertThat(region.getCountries()).containsOnly(countryBack);
        assertThat(countryBack.getRegion()).isEqualTo(region);

        region.removeCountry(countryBack);
        assertThat(region.getCountries()).doesNotContain(countryBack);
        assertThat(countryBack.getRegion()).isNull();

        region.countries(new HashSet<>(Set.of(countryBack)));
        assertThat(region.getCountries()).containsOnly(countryBack);
        assertThat(countryBack.getRegion()).isEqualTo(region);

        region.setCountries(new HashSet<>());
        assertThat(region.getCountries()).doesNotContain(countryBack);
        assertThat(countryBack.getRegion()).isNull();
    }
}
