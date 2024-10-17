package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstCustomerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstCustomerDTO.class);
        MstCustomerDTO mstCustomerDTO1 = new MstCustomerDTO();
        mstCustomerDTO1.setId(1L);
        MstCustomerDTO mstCustomerDTO2 = new MstCustomerDTO();
        assertThat(mstCustomerDTO1).isNotEqualTo(mstCustomerDTO2);
        mstCustomerDTO2.setId(mstCustomerDTO1.getId());
        assertThat(mstCustomerDTO1).isEqualTo(mstCustomerDTO2);
        mstCustomerDTO2.setId(2L);
        assertThat(mstCustomerDTO1).isNotEqualTo(mstCustomerDTO2);
        mstCustomerDTO1.setId(null);
        assertThat(mstCustomerDTO1).isNotEqualTo(mstCustomerDTO2);
    }
}
