package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubDistrictDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubDistrictDTO.class);
        SubDistrictDTO subDistrictDTO1 = new SubDistrictDTO();
        subDistrictDTO1.setId(1L);
        SubDistrictDTO subDistrictDTO2 = new SubDistrictDTO();
        assertThat(subDistrictDTO1).isNotEqualTo(subDistrictDTO2);
        subDistrictDTO2.setId(subDistrictDTO1.getId());
        assertThat(subDistrictDTO1).isEqualTo(subDistrictDTO2);
        subDistrictDTO2.setId(2L);
        assertThat(subDistrictDTO1).isNotEqualTo(subDistrictDTO2);
        subDistrictDTO1.setId(null);
        assertThat(subDistrictDTO1).isNotEqualTo(subDistrictDTO2);
    }
}
