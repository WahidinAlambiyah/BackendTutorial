package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxOrderHistoryAsserts.*;
import static com.mycompany.myapp.domain.TrxOrderHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxOrderHistoryMapperTest {

    private TrxOrderHistoryMapper trxOrderHistoryMapper;

    @BeforeEach
    void setUp() {
        trxOrderHistoryMapper = new TrxOrderHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxOrderHistorySample1();
        var actual = trxOrderHistoryMapper.toEntity(trxOrderHistoryMapper.toDto(expected));
        assertTrxOrderHistoryAllPropertiesEquals(expected, actual);
    }
}
