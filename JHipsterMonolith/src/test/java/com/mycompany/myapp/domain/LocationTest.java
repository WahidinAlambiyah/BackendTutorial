package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DepartmentTestSamples.*;
import static com.mycompany.myapp.domain.LocationTestSamples.*;
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
    void departmentTest() {
        Location location = getLocationRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        location.setDepartment(departmentBack);
        assertThat(location.getDepartment()).isEqualTo(departmentBack);
        assertThat(departmentBack.getLocation()).isEqualTo(location);

        location.department(null);
        assertThat(location.getDepartment()).isNull();
        assertThat(departmentBack.getLocation()).isNull();
    }
}
