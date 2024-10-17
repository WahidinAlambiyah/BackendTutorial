package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxProductHistoryAsserts.*;
import static com.mycompany.myapp.domain.TrxProductHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxProductHistoryMapperTest {

    private TrxProductHistoryMapper trxProductHistoryMapper;

    @BeforeEach
    void setUp() {
        trxProductHistoryMapper = new TrxProductHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxProductHistorySample1();
        var actual = trxProductHistoryMapper.toEntity(trxProductHistoryMapper.toDto(expected));
        assertTrxProductHistoryAllPropertiesEquals(expected, actual);
    }
}
