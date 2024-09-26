package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstCityAsserts.*;
import static com.mycompany.myapp.domain.MstCityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstCityMapperTest {

    private MstCityMapper mstCityMapper;

    @BeforeEach
    void setUp() {
        mstCityMapper = new MstCityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstCitySample1();
        var actual = mstCityMapper.toEntity(mstCityMapper.toDto(expected));
        assertMstCityAllPropertiesEquals(expected, actual);
    }
}
