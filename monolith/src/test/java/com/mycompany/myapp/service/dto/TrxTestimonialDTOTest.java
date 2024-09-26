package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxTestimonialDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxTestimonialDTO.class);
        TrxTestimonialDTO trxTestimonialDTO1 = new TrxTestimonialDTO();
        trxTestimonialDTO1.setId(1L);
        TrxTestimonialDTO trxTestimonialDTO2 = new TrxTestimonialDTO();
        assertThat(trxTestimonialDTO1).isNotEqualTo(trxTestimonialDTO2);
        trxTestimonialDTO2.setId(trxTestimonialDTO1.getId());
        assertThat(trxTestimonialDTO1).isEqualTo(trxTestimonialDTO2);
        trxTestimonialDTO2.setId(2L);
        assertThat(trxTestimonialDTO1).isNotEqualTo(trxTestimonialDTO2);
        trxTestimonialDTO1.setId(null);
        assertThat(trxTestimonialDTO1).isNotEqualTo(trxTestimonialDTO2);
    }
}
