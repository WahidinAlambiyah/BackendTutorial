package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstBrandAsserts.*;
import static com.mycompany.myapp.domain.MstBrandTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstBrandMapperTest {

    private MstBrandMapper mstBrandMapper;

    @BeforeEach
    void setUp() {
        mstBrandMapper = new MstBrandMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstBrandSample1();
        var actual = mstBrandMapper.toEntity(mstBrandMapper.toDto(expected));
        assertMstBrandAllPropertiesEquals(expected, actual);
    }
}
