package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstRegionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstRegionDTO.class);
        MstRegionDTO mstRegionDTO1 = new MstRegionDTO();
        mstRegionDTO1.setId(1L);
        MstRegionDTO mstRegionDTO2 = new MstRegionDTO();
        assertThat(mstRegionDTO1).isNotEqualTo(mstRegionDTO2);
        mstRegionDTO2.setId(mstRegionDTO1.getId());
        assertThat(mstRegionDTO1).isEqualTo(mstRegionDTO2);
        mstRegionDTO2.setId(2L);
        assertThat(mstRegionDTO1).isNotEqualTo(mstRegionDTO2);
        mstRegionDTO1.setId(null);
        assertThat(mstRegionDTO1).isNotEqualTo(mstRegionDTO2);
    }
}
