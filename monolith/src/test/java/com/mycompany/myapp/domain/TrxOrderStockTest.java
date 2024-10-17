package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstSupplierTestSamples.*;
import static com.mycompany.myapp.domain.TrxOrderStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrxOrderStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxOrderStock.class);
        TrxOrderStock trxOrderStock1 = getTrxOrderStockSample1();
        TrxOrderStock trxOrderStock2 = new TrxOrderStock();
        assertThat(trxOrderStock1).isNotEqualTo(trxOrderStock2);

        trxOrderStock2.setId(trxOrderStock1.getId());
        assertThat(trxOrderStock1).isEqualTo(trxOrderStock2);

        trxOrderStock2 = getTrxOrderStockSample2();
        assertThat(trxOrderStock1).isNotEqualTo(trxOrderStock2);
    }

    @Test
    void supplierTest() {
        TrxOrderStock trxOrderStock = getTrxOrderStockRandomSampleGenerator();
        MstSupplier mstSupplierBack = getMstSupplierRandomSampleGenerator();

        trxOrderStock.setSupplier(mstSupplierBack);
        assertThat(trxOrderStock.getSupplier()).isEqualTo(mstSupplierBack);

        trxOrderStock.supplier(null);
        assertThat(trxOrderStock.getSupplier()).isNull();
    }
}
