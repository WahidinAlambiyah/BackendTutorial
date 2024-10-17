package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstCategoryDTO.class);
        MstCategoryDTO mstCategoryDTO1 = new MstCategoryDTO();
        mstCategoryDTO1.setId(1L);
        MstCategoryDTO mstCategoryDTO2 = new MstCategoryDTO();
        assertThat(mstCategoryDTO1).isNotEqualTo(mstCategoryDTO2);
        mstCategoryDTO2.setId(mstCategoryDTO1.getId());
        assertThat(mstCategoryDTO1).isEqualTo(mstCategoryDTO2);
        mstCategoryDTO2.setId(2L);
        assertThat(mstCategoryDTO1).isNotEqualTo(mstCategoryDTO2);
        mstCategoryDTO1.setId(null);
        assertThat(mstCategoryDTO1).isNotEqualTo(mstCategoryDTO2);
    }
}
