package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxCartAsserts.*;
import static com.mycompany.myapp.domain.TrxCartTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxCartMapperTest {

    private TrxCartMapper trxCartMapper;

    @BeforeEach
    void setUp() {
        trxCartMapper = new TrxCartMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxCartSample1();
        var actual = trxCartMapper.toEntity(trxCartMapper.toDto(expected));
        assertTrxCartAllPropertiesEquals(expected, actual);
    }
}
