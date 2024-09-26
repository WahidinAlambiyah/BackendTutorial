package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstPostalCodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstPostalCodeDTO.class);
        MstPostalCodeDTO mstPostalCodeDTO1 = new MstPostalCodeDTO();
        mstPostalCodeDTO1.setId(1L);
        MstPostalCodeDTO mstPostalCodeDTO2 = new MstPostalCodeDTO();
        assertThat(mstPostalCodeDTO1).isNotEqualTo(mstPostalCodeDTO2);
        mstPostalCodeDTO2.setId(mstPostalCodeDTO1.getId());
        assertThat(mstPostalCodeDTO1).isEqualTo(mstPostalCodeDTO2);
        mstPostalCodeDTO2.setId(2L);
        assertThat(mstPostalCodeDTO1).isNotEqualTo(mstPostalCodeDTO2);
        mstPostalCodeDTO1.setId(null);
        assertThat(mstPostalCodeDTO1).isNotEqualTo(mstPostalCodeDTO2);
    }
}
