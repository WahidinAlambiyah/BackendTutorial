package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstJobTestSamples.*;
import static com.mycompany.myapp.domain.MstTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstTask.class);
        MstTask mstTask1 = getMstTaskSample1();
        MstTask mstTask2 = new MstTask();
        assertThat(mstTask1).isNotEqualTo(mstTask2);

        mstTask2.setId(mstTask1.getId());
        assertThat(mstTask1).isEqualTo(mstTask2);

        mstTask2 = getMstTaskSample2();
        assertThat(mstTask1).isNotEqualTo(mstTask2);
    }

    @Test
    void jobTest() {
        MstTask mstTask = getMstTaskRandomSampleGenerator();
        MstJob mstJobBack = getMstJobRandomSampleGenerator();

        mstTask.addJob(mstJobBack);
        assertThat(mstTask.getJobs()).containsOnly(mstJobBack);
        assertThat(mstJobBack.getTasks()).containsOnly(mstTask);

        mstTask.removeJob(mstJobBack);
        assertThat(mstTask.getJobs()).doesNotContain(mstJobBack);
        assertThat(mstJobBack.getTasks()).doesNotContain(mstTask);

        mstTask.jobs(new HashSet<>(Set.of(mstJobBack)));
        assertThat(mstTask.getJobs()).containsOnly(mstJobBack);
        assertThat(mstJobBack.getTasks()).containsOnly(mstTask);

        mstTask.setJobs(new HashSet<>());
        assertThat(mstTask.getJobs()).doesNotContain(mstJobBack);
        assertThat(mstJobBack.getTasks()).doesNotContain(mstTask);
    }
}
