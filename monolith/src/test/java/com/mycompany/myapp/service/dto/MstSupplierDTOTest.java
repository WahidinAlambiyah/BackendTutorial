package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstSupplierDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstSupplierDTO.class);
        MstSupplierDTO mstSupplierDTO1 = new MstSupplierDTO();
        mstSupplierDTO1.setId(1L);
        MstSupplierDTO mstSupplierDTO2 = new MstSupplierDTO();
        assertThat(mstSupplierDTO1).isNotEqualTo(mstSupplierDTO2);
        mstSupplierDTO2.setId(mstSupplierDTO1.getId());
        assertThat(mstSupplierDTO1).isEqualTo(mstSupplierDTO2);
        mstSupplierDTO2.setId(2L);
        assertThat(mstSupplierDTO1).isNotEqualTo(mstSupplierDTO2);
        mstSupplierDTO1.setId(null);
        assertThat(mstSupplierDTO1).isNotEqualTo(mstSupplierDTO2);
    }
}
