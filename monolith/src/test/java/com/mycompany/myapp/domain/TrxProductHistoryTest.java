package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TrxProductHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxProductHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxProductHistory.class);
        TrxProductHistory trxProductHistory1 = getTrxProductHistorySample1();
        TrxProductHistory trxProductHistory2 = new TrxProductHistory();
        assertThat(trxProductHistory1).isNotEqualTo(trxProductHistory2);

        trxProductHistory2.setId(trxProductHistory1.getId());
        assertThat(trxProductHistory1).isEqualTo(trxProductHistory2);

        trxProductHistory2 = getTrxProductHistorySample2();
        assertThat(trxProductHistory1).isNotEqualTo(trxProductHistory2);
    }
}
