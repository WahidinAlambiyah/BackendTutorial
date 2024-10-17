package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxOrderItemAsserts.*;
import static com.mycompany.myapp.domain.TrxOrderItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxOrderItemMapperTest {

    private TrxOrderItemMapper trxOrderItemMapper;

    @BeforeEach
    void setUp() {
        trxOrderItemMapper = new TrxOrderItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxOrderItemSample1();
        var actual = trxOrderItemMapper.toEntity(trxOrderItemMapper.toDto(expected));
        assertTrxOrderItemAllPropertiesEquals(expected, actual);
    }
}
