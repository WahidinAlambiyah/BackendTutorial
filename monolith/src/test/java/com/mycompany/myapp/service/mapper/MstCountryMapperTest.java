package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstCountryAsserts.*;
import static com.mycompany.myapp.domain.MstCountryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstCountryMapperTest {

    private MstCountryMapper mstCountryMapper;

    @BeforeEach
    void setUp() {
        mstCountryMapper = new MstCountryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstCountrySample1();
        var actual = mstCountryMapper.toEntity(mstCountryMapper.toDto(expected));
        assertMstCountryAllPropertiesEquals(expected, actual);
    }
}
