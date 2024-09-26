package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.JobHistoryTestSamples.*;
import static com.mycompany.myapp.domain.LocationTestSamples.*;
import static com.mycompany.myapp.domain.MstDepartmentTestSamples.*;
import static com.mycompany.myapp.domain.MstEmployeeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstDepartmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstDepartment.class);
        MstDepartment mstDepartment1 = getMstDepartmentSample1();
        MstDepartment mstDepartment2 = new MstDepartment();
        assertThat(mstDepartment1).isNotEqualTo(mstDepartment2);

        mstDepartment2.setId(mstDepartment1.getId());
        assertThat(mstDepartment1).isEqualTo(mstDepartment2);

        mstDepartment2 = getMstDepartmentSample2();
        assertThat(mstDepartment1).isNotEqualTo(mstDepartment2);
    }

    @Test
    void locationTest() {
        MstDepartment mstDepartment = getMstDepartmentRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        mstDepartment.setLocation(locationBack);
        assertThat(mstDepartment.getLocation()).isEqualTo(locationBack);

        mstDepartment.location(null);
        assertThat(mstDepartment.getLocation()).isNull();
    }

    @Test
    void mstEmployeeTest() {
        MstDepartment mstDepartment = getMstDepartmentRandomSampleGenerator();
        MstEmployee mstEmployeeBack = getMstEmployeeRandomSampleGenerator();

        mstDepartment.addMstEmployee(mstEmployeeBack);
        assertThat(mstDepartment.getMstEmployees()).containsOnly(mstEmployeeBack);
        assertThat(mstEmployeeBack.getDepartment()).isEqualTo(mstDepartment);

        mstDepartment.removeMstEmployee(mstEmployeeBack);
        assertThat(mstDepartment.getMstEmployees()).doesNotContain(mstEmployeeBack);
        assertThat(mstEmployeeBack.getDepartment()).isNull();

        mstDepartment.mstEmployees(new HashSet<>(Set.of(mstEmployeeBack)));
        assertThat(mstDepartment.getMstEmployees()).containsOnly(mstEmployeeBack);
        assertThat(mstEmployeeBack.getDepartment()).isEqualTo(mstDepartment);

        mstDepartment.setMstEmployees(new HashSet<>());
        assertThat(mstDepartment.getMstEmployees()).doesNotContain(mstEmployeeBack);
        assertThat(mstEmployeeBack.getDepartment()).isNull();
    }

    @Test
    void jobHistoryTest() {
        MstDepartment mstDepartment = getMstDepartmentRandomSampleGenerator();
        JobHistory jobHistoryBack = getJobHistoryRandomSampleGenerator();

        mstDepartment.setJobHistory(jobHistoryBack);
        assertThat(mstDepartment.getJobHistory()).isEqualTo(jobHistoryBack);
        assertThat(jobHistoryBack.getDepartment()).isEqualTo(mstDepartment);

        mstDepartment.jobHistory(null);
        assertThat(mstDepartment.getJobHistory()).isNull();
        assertThat(jobHistoryBack.getDepartment()).isNull();
    }
}
