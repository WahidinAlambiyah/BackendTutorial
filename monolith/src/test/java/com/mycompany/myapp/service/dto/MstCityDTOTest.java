package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstCityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstCityDTO.class);
        MstCityDTO mstCityDTO1 = new MstCityDTO();
        mstCityDTO1.setId(1L);
        MstCityDTO mstCityDTO2 = new MstCityDTO();
        assertThat(mstCityDTO1).isNotEqualTo(mstCityDTO2);
        mstCityDTO2.setId(mstCityDTO1.getId());
        assertThat(mstCityDTO1).isEqualTo(mstCityDTO2);
        mstCityDTO2.setId(2L);
        assertThat(mstCityDTO1).isNotEqualTo(mstCityDTO2);
        mstCityDTO1.setId(null);
        assertThat(mstCityDTO1).isNotEqualTo(mstCityDTO2);
    }
}
