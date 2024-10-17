package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.JobHistoryTestSamples.*;
import static com.mycompany.myapp.domain.MstDepartmentTestSamples.*;
import static com.mycompany.myapp.domain.MstEmployeeTestSamples.*;
import static com.mycompany.myapp.domain.MstEmployeeTestSamples.*;
import static com.mycompany.myapp.domain.MstJobTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstEmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstEmployee.class);
        MstEmployee mstEmployee1 = getMstEmployeeSample1();
        MstEmployee mstEmployee2 = new MstEmployee();
        assertThat(mstEmployee1).isNotEqualTo(mstEmployee2);

        mstEmployee2.setId(mstEmployee1.getId());
        assertThat(mstEmployee1).isEqualTo(mstEmployee2);

        mstEmployee2 = getMstEmployeeSample2();
        assertThat(mstEmployee1).isNotEqualTo(mstEmployee2);
    }

    @Test
    void mstJobTest() {
        MstEmployee mstEmployee = getMstEmployeeRandomSampleGenerator();
        MstJob mstJobBack = getMstJobRandomSampleGenerator();

        mstEmployee.addMstJob(mstJobBack);
        assertThat(mstEmployee.getMstJobs()).containsOnly(mstJobBack);
        assertThat(mstJobBack.getEmployee()).isEqualTo(mstEmployee);

        mstEmployee.removeMstJob(mstJobBack);
        assertThat(mstEmployee.getMstJobs()).doesNotContain(mstJobBack);
        assertThat(mstJobBack.getEmployee()).isNull();

        mstEmployee.mstJobs(new HashSet<>(Set.of(mstJobBack)));
        assertThat(mstEmployee.getMstJobs()).containsOnly(mstJobBack);
        assertThat(mstJobBack.getEmployee()).isEqualTo(mstEmployee);

        mstEmployee.setMstJobs(new HashSet<>());
        assertThat(mstEmployee.getMstJobs()).doesNotContain(mstJobBack);
        assertThat(mstJobBack.getEmployee()).isNull();
    }

    @Test
    void managerTest() {
        MstEmployee mstEmployee = getMstEmployeeRandomSampleGenerator();
        MstEmployee mstEmployeeBack = getMstEmployeeRandomSampleGenerator();

        mstEmployee.setManager(mstEmployeeBack);
        assertThat(mstEmployee.getManager()).isEqualTo(mstEmployeeBack);

        mstEmployee.manager(null);
        assertThat(mstEmployee.getManager()).isNull();
    }

    @Test
    void departmentTest() {
        MstEmployee mstEmployee = getMstEmployeeRandomSampleGenerator();
        MstDepartment mstDepartmentBack = getMstDepartmentRandomSampleGenerator();

        mstEmployee.setDepartment(mstDepartmentBack);
        assertThat(mstEmployee.getDepartment()).isEqualTo(mstDepartmentBack);

        mstEmployee.department(null);
        assertThat(mstEmployee.getDepartment()).isNull();
    }

    @Test
    void mstDepartmentTest() {
        MstEmployee mstEmployee = getMstEmployeeRandomSampleGenerator();
        MstDepartment mstDepartmentBack = getMstDepartmentRandomSampleGenerator();

        mstEmployee.setMstDepartment(mstDepartmentBack);
        assertThat(mstEmployee.getMstDepartment()).isEqualTo(mstDepartmentBack);

        mstEmployee.mstDepartment(null);
        assertThat(mstEmployee.getMstDepartment()).isNull();
    }

    @Test
    void jobHistoryTest() {
        MstEmployee mstEmployee = getMstEmployeeRandomSampleGenerator();
        JobHistory jobHistoryBack = getJobHistoryRandomSampleGenerator();

        mstEmployee.setJobHistory(jobHistoryBack);
        assertThat(mstEmployee.getJobHistory()).isEqualTo(jobHistoryBack);
        assertThat(jobHistoryBack.getEmployee()).isEqualTo(mstEmployee);

        mstEmployee.jobHistory(null);
        assertThat(mstEmployee.getJobHistory()).isNull();
        assertThat(jobHistoryBack.getEmployee()).isNull();
    }
}
