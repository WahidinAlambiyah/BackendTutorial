package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxOrderStockAsserts.*;
import static com.mycompany.myapp.domain.TrxOrderStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxOrderStockMapperTest {

    private TrxOrderStockMapper trxOrderStockMapper;

    @BeforeEach
    void setUp() {
        trxOrderStockMapper = new TrxOrderStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxOrderStockSample1();
        var actual = trxOrderStockMapper.toEntity(trxOrderStockMapper.toDto(expected));
        assertTrxOrderStockAllPropertiesEquals(expected, actual);
    }
}
