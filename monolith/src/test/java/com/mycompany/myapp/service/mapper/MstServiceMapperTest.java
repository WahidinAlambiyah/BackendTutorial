package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstServiceAsserts.*;
import static com.mycompany.myapp.domain.MstServiceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstServiceMapperTest {

    private MstServiceMapper mstServiceMapper;

    @BeforeEach
    void setUp() {
        mstServiceMapper = new MstServiceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstServiceSample1();
        var actual = mstServiceMapper.toEntity(mstServiceMapper.toDto(expected));
        assertMstServiceAllPropertiesEquals(expected, actual);
    }
}
