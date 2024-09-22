package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostalCodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostalCodeDTO.class);
        PostalCodeDTO postalCodeDTO1 = new PostalCodeDTO();
        postalCodeDTO1.setId(1L);
        PostalCodeDTO postalCodeDTO2 = new PostalCodeDTO();
        assertThat(postalCodeDTO1).isNotEqualTo(postalCodeDTO2);
        postalCodeDTO2.setId(postalCodeDTO1.getId());
        assertThat(postalCodeDTO1).isEqualTo(postalCodeDTO2);
        postalCodeDTO2.setId(2L);
        assertThat(postalCodeDTO1).isNotEqualTo(postalCodeDTO2);
        postalCodeDTO1.setId(null);
        assertThat(postalCodeDTO1).isNotEqualTo(postalCodeDTO2);
    }
}
