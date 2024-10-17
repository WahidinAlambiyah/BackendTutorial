package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxDiscountAsserts.*;
import static com.mycompany.myapp.domain.TrxDiscountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxDiscountMapperTest {

    private TrxDiscountMapper trxDiscountMapper;

    @BeforeEach
    void setUp() {
        trxDiscountMapper = new TrxDiscountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxDiscountSample1();
        var actual = trxDiscountMapper.toEntity(trxDiscountMapper.toDto(expected));
        assertTrxDiscountAllPropertiesEquals(expected, actual);
    }
}
