package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.LocationTestSamples.*;
import static com.mycompany.myapp.domain.MstDepartmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Location.class);
        Location location1 = getLocationSample1();
        Location location2 = new Location();
        assertThat(location1).isNotEqualTo(location2);

        location2.setId(location1.getId());
        assertThat(location1).isEqualTo(location2);

        location2 = getLocationSample2();
        assertThat(location1).isNotEqualTo(location2);
    }

    @Test
    void mstDepartmentTest() {
        Location location = getLocationRandomSampleGenerator();
        MstDepartment mstDepartmentBack = getMstDepartmentRandomSampleGenerator();

        location.setMstDepartment(mstDepartmentBack);
        assertThat(location.getMstDepartment()).isEqualTo(mstDepartmentBack);
        assertThat(mstDepartmentBack.getLocation()).isEqualTo(location);

        location.mstDepartment(null);
        assertThat(location.getMstDepartment()).isNull();
        assertThat(mstDepartmentBack.getLocation()).isNull();
    }
}
