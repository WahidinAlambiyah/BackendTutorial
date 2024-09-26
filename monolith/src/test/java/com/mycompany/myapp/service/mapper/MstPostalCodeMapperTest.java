package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstPostalCodeAsserts.*;
import static com.mycompany.myapp.domain.MstPostalCodeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstPostalCodeMapperTest {

    private MstPostalCodeMapper mstPostalCodeMapper;

    @BeforeEach
    void setUp() {
        mstPostalCodeMapper = new MstPostalCodeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstPostalCodeSample1();
        var actual = mstPostalCodeMapper.toEntity(mstPostalCodeMapper.toDto(expected));
        assertMstPostalCodeAllPropertiesEquals(expected, actual);
    }
}
