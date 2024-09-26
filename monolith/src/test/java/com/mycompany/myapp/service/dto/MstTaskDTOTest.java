package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstTaskDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstTaskDTO.class);
        MstTaskDTO mstTaskDTO1 = new MstTaskDTO();
        mstTaskDTO1.setId(1L);
        MstTaskDTO mstTaskDTO2 = new MstTaskDTO();
        assertThat(mstTaskDTO1).isNotEqualTo(mstTaskDTO2);
        mstTaskDTO2.setId(mstTaskDTO1.getId());
        assertThat(mstTaskDTO1).isEqualTo(mstTaskDTO2);
        mstTaskDTO2.setId(2L);
        assertThat(mstTaskDTO1).isNotEqualTo(mstTaskDTO2);
        mstTaskDTO1.setId(null);
        assertThat(mstTaskDTO1).isNotEqualTo(mstTaskDTO2);
    }
}
