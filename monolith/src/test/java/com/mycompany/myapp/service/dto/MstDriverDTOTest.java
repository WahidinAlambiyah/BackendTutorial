package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstDriverDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstDriverDTO.class);
        MstDriverDTO mstDriverDTO1 = new MstDriverDTO();
        mstDriverDTO1.setId(1L);
        MstDriverDTO mstDriverDTO2 = new MstDriverDTO();
        assertThat(mstDriverDTO1).isNotEqualTo(mstDriverDTO2);
        mstDriverDTO2.setId(mstDriverDTO1.getId());
        assertThat(mstDriverDTO1).isEqualTo(mstDriverDTO2);
        mstDriverDTO2.setId(2L);
        assertThat(mstDriverDTO1).isNotEqualTo(mstDriverDTO2);
        mstDriverDTO1.setId(null);
        assertThat(mstDriverDTO1).isNotEqualTo(mstDriverDTO2);
    }
}
