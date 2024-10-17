package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxDeliveryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxDeliveryDTO.class);
        TrxDeliveryDTO trxDeliveryDTO1 = new TrxDeliveryDTO();
        trxDeliveryDTO1.setId(1L);
        TrxDeliveryDTO trxDeliveryDTO2 = new TrxDeliveryDTO();
        assertThat(trxDeliveryDTO1).isNotEqualTo(trxDeliveryDTO2);
        trxDeliveryDTO2.setId(trxDeliveryDTO1.getId());
        assertThat(trxDeliveryDTO1).isEqualTo(trxDeliveryDTO2);
        trxDeliveryDTO2.setId(2L);
        assertThat(trxDeliveryDTO1).isNotEqualTo(trxDeliveryDTO2);
        trxDeliveryDTO1.setId(null);
        assertThat(trxDeliveryDTO1).isNotEqualTo(trxDeliveryDTO2);
    }
}
