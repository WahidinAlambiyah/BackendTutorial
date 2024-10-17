package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstProductDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstProductDTO.class);
        MstProductDTO mstProductDTO1 = new MstProductDTO();
        mstProductDTO1.setId(1L);
        MstProductDTO mstProductDTO2 = new MstProductDTO();
        assertThat(mstProductDTO1).isNotEqualTo(mstProductDTO2);
        mstProductDTO2.setId(mstProductDTO1.getId());
        assertThat(mstProductDTO1).isEqualTo(mstProductDTO2);
        mstProductDTO2.setId(2L);
        assertThat(mstProductDTO1).isNotEqualTo(mstProductDTO2);
        mstProductDTO1.setId(null);
        assertThat(mstProductDTO1).isNotEqualTo(mstProductDTO2);
    }
}
