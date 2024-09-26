package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstDepartmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstDepartmentDTO.class);
        MstDepartmentDTO mstDepartmentDTO1 = new MstDepartmentDTO();
        mstDepartmentDTO1.setId(1L);
        MstDepartmentDTO mstDepartmentDTO2 = new MstDepartmentDTO();
        assertThat(mstDepartmentDTO1).isNotEqualTo(mstDepartmentDTO2);
        mstDepartmentDTO2.setId(mstDepartmentDTO1.getId());
        assertThat(mstDepartmentDTO1).isEqualTo(mstDepartmentDTO2);
        mstDepartmentDTO2.setId(2L);
        assertThat(mstDepartmentDTO1).isNotEqualTo(mstDepartmentDTO2);
        mstDepartmentDTO1.setId(null);
        assertThat(mstDepartmentDTO1).isNotEqualTo(mstDepartmentDTO2);
    }
}
