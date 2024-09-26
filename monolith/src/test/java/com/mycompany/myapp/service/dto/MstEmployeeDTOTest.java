package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstEmployeeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstEmployeeDTO.class);
        MstEmployeeDTO mstEmployeeDTO1 = new MstEmployeeDTO();
        mstEmployeeDTO1.setId(1L);
        MstEmployeeDTO mstEmployeeDTO2 = new MstEmployeeDTO();
        assertThat(mstEmployeeDTO1).isNotEqualTo(mstEmployeeDTO2);
        mstEmployeeDTO2.setId(mstEmployeeDTO1.getId());
        assertThat(mstEmployeeDTO1).isEqualTo(mstEmployeeDTO2);
        mstEmployeeDTO2.setId(2L);
        assertThat(mstEmployeeDTO1).isNotEqualTo(mstEmployeeDTO2);
        mstEmployeeDTO1.setId(null);
        assertThat(mstEmployeeDTO1).isNotEqualTo(mstEmployeeDTO2);
    }
}
