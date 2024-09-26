package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstProvinceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstProvinceDTO.class);
        MstProvinceDTO mstProvinceDTO1 = new MstProvinceDTO();
        mstProvinceDTO1.setId(1L);
        MstProvinceDTO mstProvinceDTO2 = new MstProvinceDTO();
        assertThat(mstProvinceDTO1).isNotEqualTo(mstProvinceDTO2);
        mstProvinceDTO2.setId(mstProvinceDTO1.getId());
        assertThat(mstProvinceDTO1).isEqualTo(mstProvinceDTO2);
        mstProvinceDTO2.setId(2L);
        assertThat(mstProvinceDTO1).isNotEqualTo(mstProvinceDTO2);
        mstProvinceDTO1.setId(null);
        assertThat(mstProvinceDTO1).isNotEqualTo(mstProvinceDTO2);
    }
}
