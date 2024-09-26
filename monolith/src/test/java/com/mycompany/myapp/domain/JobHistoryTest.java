package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.JobHistoryTestSamples.*;
import static com.mycompany.myapp.domain.MstDepartmentTestSamples.*;
import static com.mycompany.myapp.domain.MstEmployeeTestSamples.*;
import static com.mycompany.myapp.domain.MstJobTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JobHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobHistory.class);
        JobHistory jobHistory1 = getJobHistorySample1();
        JobHistory jobHistory2 = new JobHistory();
        assertThat(jobHistory1).isNotEqualTo(jobHistory2);

        jobHistory2.setId(jobHistory1.getId());
        assertThat(jobHistory1).isEqualTo(jobHistory2);

        jobHistory2 = getJobHistorySample2();
        assertThat(jobHistory1).isNotEqualTo(jobHistory2);
    }

    @Test
    void jobTest() {
        JobHistory jobHistory = getJobHistoryRandomSampleGenerator();
        MstJob mstJobBack = getMstJobRandomSampleGenerator();

        jobHistory.setJob(mstJobBack);
        assertThat(jobHistory.getJob()).isEqualTo(mstJobBack);

        jobHistory.job(null);
        assertThat(jobHistory.getJob()).isNull();
    }

    @Test
    void departmentTest() {
        JobHistory jobHistory = getJobHistoryRandomSampleGenerator();
        MstDepartment mstDepartmentBack = getMstDepartmentRandomSampleGenerator();

        jobHistory.setDepartment(mstDepartmentBack);
        assertThat(jobHistory.getDepartment()).isEqualTo(mstDepartmentBack);

        jobHistory.department(null);
        assertThat(jobHistory.getDepartment()).isNull();
    }

    @Test
    void employeeTest() {
        JobHistory jobHistory = getJobHistoryRandomSampleGenerator();
        MstEmployee mstEmployeeBack = getMstEmployeeRandomSampleGenerator();

        jobHistory.setEmployee(mstEmployeeBack);
        assertThat(jobHistory.getEmployee()).isEqualTo(mstEmployeeBack);

        jobHistory.employee(null);
        assertThat(jobHistory.getEmployee()).isNull();
    }
}
