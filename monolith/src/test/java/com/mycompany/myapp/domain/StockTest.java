package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstProductTestSamples.*;
import static com.mycompany.myapp.domain.StockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Stock.class);
        Stock stock1 = getStockSample1();
        Stock stock2 = new Stock();
        assertThat(stock1).isNotEqualTo(stock2);

        stock2.setId(stock1.getId());
        assertThat(stock1).isEqualTo(stock2);

        stock2 = getStockSample2();
        assertThat(stock1).isNotEqualTo(stock2);
    }

    @Test
    void productTest() {
        Stock stock = getStockRandomSampleGenerator();
        MstProduct mstProductBack = getMstProductRandomSampleGenerator();

        stock.setProduct(mstProductBack);
        assertThat(stock.getProduct()).isEqualTo(mstProductBack);

        stock.product(null);
        assertThat(stock.getProduct()).isNull();
    }
}
