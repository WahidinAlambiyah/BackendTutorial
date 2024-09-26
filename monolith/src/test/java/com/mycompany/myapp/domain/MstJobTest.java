package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.JobHistoryTestSamples.*;
import static com.mycompany.myapp.domain.MstEmployeeTestSamples.*;
import static com.mycompany.myapp.domain.MstJobTestSamples.*;
import static com.mycompany.myapp.domain.MstTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstJobTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstJob.class);
        MstJob mstJob1 = getMstJobSample1();
        MstJob mstJob2 = new MstJob();
        assertThat(mstJob1).isNotEqualTo(mstJob2);

        mstJob2.setId(mstJob1.getId());
        assertThat(mstJob1).isEqualTo(mstJob2);

        mstJob2 = getMstJobSample2();
        assertThat(mstJob1).isNotEqualTo(mstJob2);
    }

    @Test
    void taskTest() {
        MstJob mstJob = getMstJobRandomSampleGenerator();
        MstTask mstTaskBack = getMstTaskRandomSampleGenerator();

        mstJob.addTask(mstTaskBack);
        assertThat(mstJob.getTasks()).containsOnly(mstTaskBack);

        mstJob.removeTask(mstTaskBack);
        assertThat(mstJob.getTasks()).doesNotContain(mstTaskBack);

        mstJob.tasks(new HashSet<>(Set.of(mstTaskBack)));
        assertThat(mstJob.getTasks()).containsOnly(mstTaskBack);

        mstJob.setTasks(new HashSet<>());
        assertThat(mstJob.getTasks()).doesNotContain(mstTaskBack);
    }

    @Test
    void employeeTest() {
        MstJob mstJob = getMstJobRandomSampleGenerator();
        MstEmployee mstEmployeeBack = getMstEmployeeRandomSampleGenerator();

        mstJob.setEmployee(mstEmployeeBack);
        assertThat(mstJob.getEmployee()).isEqualTo(mstEmployeeBack);

        mstJob.employee(null);
        assertThat(mstJob.getEmployee()).isNull();
    }

    @Test
    void jobHistoryTest() {
        MstJob mstJob = getMstJobRandomSampleGenerator();
        JobHistory jobHistoryBack = getJobHistoryRandomSampleGenerator();

        mstJob.setJobHistory(jobHistoryBack);
        assertThat(mstJob.getJobHistory()).isEqualTo(jobHistoryBack);
        assertThat(jobHistoryBack.getJob()).isEqualTo(mstJob);

        mstJob.jobHistory(null);
        assertThat(mstJob.getJobHistory()).isNull();
        assertThat(jobHistoryBack.getJob()).isNull();
    }
}
