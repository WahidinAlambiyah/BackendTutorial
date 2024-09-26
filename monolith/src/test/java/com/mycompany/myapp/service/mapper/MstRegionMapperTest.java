package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstRegionAsserts.*;
import static com.mycompany.myapp.domain.MstRegionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstRegionMapperTest {

    private MstRegionMapper mstRegionMapper;

    @BeforeEach
    void setUp() {
        mstRegionMapper = new MstRegionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstRegionSample1();
        var actual = mstRegionMapper.toEntity(mstRegionMapper.toDto(expected));
        assertMstRegionAllPropertiesEquals(expected, actual);
    }
}
