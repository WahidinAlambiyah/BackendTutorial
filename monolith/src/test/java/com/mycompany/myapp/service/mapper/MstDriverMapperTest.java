package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstDriverAsserts.*;
import static com.mycompany.myapp.domain.MstDriverTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstDriverMapperTest {

    private MstDriverMapper mstDriverMapper;

    @BeforeEach
    void setUp() {
        mstDriverMapper = new MstDriverMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstDriverSample1();
        var actual = mstDriverMapper.toEntity(mstDriverMapper.toDto(expected));
        assertMstDriverAllPropertiesEquals(expected, actual);
    }
}
