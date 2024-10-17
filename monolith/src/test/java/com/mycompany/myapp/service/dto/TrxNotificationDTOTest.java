package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxNotificationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxNotificationDTO.class);
        TrxNotificationDTO trxNotificationDTO1 = new TrxNotificationDTO();
        trxNotificationDTO1.setId(1L);
        TrxNotificationDTO trxNotificationDTO2 = new TrxNotificationDTO();
        assertThat(trxNotificationDTO1).isNotEqualTo(trxNotificationDTO2);
        trxNotificationDTO2.setId(trxNotificationDTO1.getId());
        assertThat(trxNotificationDTO1).isEqualTo(trxNotificationDTO2);
        trxNotificationDTO2.setId(2L);
        assertThat(trxNotificationDTO1).isNotEqualTo(trxNotificationDTO2);
        trxNotificationDTO1.setId(null);
        assertThat(trxNotificationDTO1).isNotEqualTo(trxNotificationDTO2);
    }
}
