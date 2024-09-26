package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstJobDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstJobDTO.class);
        MstJobDTO mstJobDTO1 = new MstJobDTO();
        mstJobDTO1.setId(1L);
        MstJobDTO mstJobDTO2 = new MstJobDTO();
        assertThat(mstJobDTO1).isNotEqualTo(mstJobDTO2);
        mstJobDTO2.setId(mstJobDTO1.getId());
        assertThat(mstJobDTO1).isEqualTo(mstJobDTO2);
        mstJobDTO2.setId(2L);
        assertThat(mstJobDTO1).isNotEqualTo(mstJobDTO2);
        mstJobDTO1.setId(null);
        assertThat(mstJobDTO1).isNotEqualTo(mstJobDTO2);
    }
}
