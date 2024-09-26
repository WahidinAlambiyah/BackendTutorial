package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstDistrictDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstDistrictDTO.class);
        MstDistrictDTO mstDistrictDTO1 = new MstDistrictDTO();
        mstDistrictDTO1.setId(1L);
        MstDistrictDTO mstDistrictDTO2 = new MstDistrictDTO();
        assertThat(mstDistrictDTO1).isNotEqualTo(mstDistrictDTO2);
        mstDistrictDTO2.setId(mstDistrictDTO1.getId());
        assertThat(mstDistrictDTO1).isEqualTo(mstDistrictDTO2);
        mstDistrictDTO2.setId(2L);
        assertThat(mstDistrictDTO1).isNotEqualTo(mstDistrictDTO2);
        mstDistrictDTO1.setId(null);
        assertThat(mstDistrictDTO1).isNotEqualTo(mstDistrictDTO2);
    }
}
