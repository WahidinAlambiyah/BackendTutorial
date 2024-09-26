package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstCountryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstCountryDTO.class);
        MstCountryDTO mstCountryDTO1 = new MstCountryDTO();
        mstCountryDTO1.setId(1L);
        MstCountryDTO mstCountryDTO2 = new MstCountryDTO();
        assertThat(mstCountryDTO1).isNotEqualTo(mstCountryDTO2);
        mstCountryDTO2.setId(mstCountryDTO1.getId());
        assertThat(mstCountryDTO1).isEqualTo(mstCountryDTO2);
        mstCountryDTO2.setId(2L);
        assertThat(mstCountryDTO1).isNotEqualTo(mstCountryDTO2);
        mstCountryDTO1.setId(null);
        assertThat(mstCountryDTO1).isNotEqualTo(mstCountryDTO2);
    }
}
