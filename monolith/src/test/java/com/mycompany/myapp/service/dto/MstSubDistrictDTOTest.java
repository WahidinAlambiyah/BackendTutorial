package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstSubDistrictDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstSubDistrictDTO.class);
        MstSubDistrictDTO mstSubDistrictDTO1 = new MstSubDistrictDTO();
        mstSubDistrictDTO1.setId(1L);
        MstSubDistrictDTO mstSubDistrictDTO2 = new MstSubDistrictDTO();
        assertThat(mstSubDistrictDTO1).isNotEqualTo(mstSubDistrictDTO2);
        mstSubDistrictDTO2.setId(mstSubDistrictDTO1.getId());
        assertThat(mstSubDistrictDTO1).isEqualTo(mstSubDistrictDTO2);
        mstSubDistrictDTO2.setId(2L);
        assertThat(mstSubDistrictDTO1).isNotEqualTo(mstSubDistrictDTO2);
        mstSubDistrictDTO1.setId(null);
        assertThat(mstSubDistrictDTO1).isNotEqualTo(mstSubDistrictDTO2);
    }
}
