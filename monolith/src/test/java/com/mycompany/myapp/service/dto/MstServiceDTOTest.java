package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstServiceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstServiceDTO.class);
        MstServiceDTO mstServiceDTO1 = new MstServiceDTO();
        mstServiceDTO1.setId(1L);
        MstServiceDTO mstServiceDTO2 = new MstServiceDTO();
        assertThat(mstServiceDTO1).isNotEqualTo(mstServiceDTO2);
        mstServiceDTO2.setId(mstServiceDTO1.getId());
        assertThat(mstServiceDTO1).isEqualTo(mstServiceDTO2);
        mstServiceDTO2.setId(2L);
        assertThat(mstServiceDTO1).isNotEqualTo(mstServiceDTO2);
        mstServiceDTO1.setId(null);
        assertThat(mstServiceDTO1).isNotEqualTo(mstServiceDTO2);
    }
}
