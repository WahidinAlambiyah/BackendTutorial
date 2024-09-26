package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxEventAsserts.*;
import static com.mycompany.myapp.domain.TrxEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxEventMapperTest {

    private TrxEventMapper trxEventMapper;

    @BeforeEach
    void setUp() {
        trxEventMapper = new TrxEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxEventSample1();
        var actual = trxEventMapper.toEntity(trxEventMapper.toDto(expected));
        assertTrxEventAllPropertiesEquals(expected, actual);
    }
}
