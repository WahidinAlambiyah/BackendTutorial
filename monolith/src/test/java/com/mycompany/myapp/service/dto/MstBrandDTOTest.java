package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstBrandDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstBrandDTO.class);
        MstBrandDTO mstBrandDTO1 = new MstBrandDTO();
        mstBrandDTO1.setId(1L);
        MstBrandDTO mstBrandDTO2 = new MstBrandDTO();
        assertThat(mstBrandDTO1).isNotEqualTo(mstBrandDTO2);
        mstBrandDTO2.setId(mstBrandDTO1.getId());
        assertThat(mstBrandDTO1).isEqualTo(mstBrandDTO2);
        mstBrandDTO2.setId(2L);
        assertThat(mstBrandDTO1).isNotEqualTo(mstBrandDTO2);
        mstBrandDTO1.setId(null);
        assertThat(mstBrandDTO1).isNotEqualTo(mstBrandDTO2);
    }
}
