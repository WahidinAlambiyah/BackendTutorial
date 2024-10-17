package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstLoyaltyProgramDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstLoyaltyProgramDTO.class);
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO1 = new MstLoyaltyProgramDTO();
        mstLoyaltyProgramDTO1.setId(1L);
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO2 = new MstLoyaltyProgramDTO();
        assertThat(mstLoyaltyProgramDTO1).isNotEqualTo(mstLoyaltyProgramDTO2);
        mstLoyaltyProgramDTO2.setId(mstLoyaltyProgramDTO1.getId());
        assertThat(mstLoyaltyProgramDTO1).isEqualTo(mstLoyaltyProgramDTO2);
        mstLoyaltyProgramDTO2.setId(2L);
        assertThat(mstLoyaltyProgramDTO1).isNotEqualTo(mstLoyaltyProgramDTO2);
        mstLoyaltyProgramDTO1.setId(null);
        assertThat(mstLoyaltyProgramDTO1).isNotEqualTo(mstLoyaltyProgramDTO2);
    }
}
