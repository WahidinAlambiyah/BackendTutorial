package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxStockAlertAsserts.*;
import static com.mycompany.myapp.domain.TrxStockAlertTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxStockAlertMapperTest {

    private TrxStockAlertMapper trxStockAlertMapper;

    @BeforeEach
    void setUp() {
        trxStockAlertMapper = new TrxStockAlertMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxStockAlertSample1();
        var actual = trxStockAlertMapper.toEntity(trxStockAlertMapper.toDto(expected));
        assertTrxStockAlertAllPropertiesEquals(expected, actual);
    }
}
