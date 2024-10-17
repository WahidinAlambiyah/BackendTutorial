package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.StockAsserts.*;
import static com.mycompany.myapp.domain.StockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockMapperTest {

    private StockMapper stockMapper;

    @BeforeEach
    void setUp() {
        stockMapper = new StockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockSample1();
        var actual = stockMapper.toEntity(stockMapper.toDto(expected));
        assertStockAllPropertiesEquals(expected, actual);
    }
}
