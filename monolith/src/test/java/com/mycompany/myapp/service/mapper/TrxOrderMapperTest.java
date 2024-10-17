package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxOrderAsserts.*;
import static com.mycompany.myapp.domain.TrxOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxOrderMapperTest {

    private TrxOrderMapper trxOrderMapper;

    @BeforeEach
    void setUp() {
        trxOrderMapper = new TrxOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxOrderSample1();
        var actual = trxOrderMapper.toEntity(trxOrderMapper.toDto(expected));
        assertTrxOrderAllPropertiesEquals(expected, actual);
    }
}
