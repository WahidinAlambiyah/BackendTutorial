package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TrxStockAlertTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxStockAlertTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxStockAlert.class);
        TrxStockAlert trxStockAlert1 = getTrxStockAlertSample1();
        TrxStockAlert trxStockAlert2 = new TrxStockAlert();
        assertThat(trxStockAlert1).isNotEqualTo(trxStockAlert2);

        trxStockAlert2.setId(trxStockAlert1.getId());
        assertThat(trxStockAlert1).isEqualTo(trxStockAlert2);

        trxStockAlert2 = getTrxStockAlertSample2();
        assertThat(trxStockAlert1).isNotEqualTo(trxStockAlert2);
    }
}
