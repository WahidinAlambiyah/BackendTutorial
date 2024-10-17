package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TrxOrderHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxOrderHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxOrderHistory.class);
        TrxOrderHistory trxOrderHistory1 = getTrxOrderHistorySample1();
        TrxOrderHistory trxOrderHistory2 = new TrxOrderHistory();
        assertThat(trxOrderHistory1).isNotEqualTo(trxOrderHistory2);

        trxOrderHistory2.setId(trxOrderHistory1.getId());
        assertThat(trxOrderHistory1).isEqualTo(trxOrderHistory2);

        trxOrderHistory2 = getTrxOrderHistorySample2();
        assertThat(trxOrderHistory1).isNotEqualTo(trxOrderHistory2);
    }
}
