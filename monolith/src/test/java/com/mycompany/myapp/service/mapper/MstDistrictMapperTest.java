package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstDistrictAsserts.*;
import static com.mycompany.myapp.domain.MstDistrictTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstDistrictMapperTest {

    private MstDistrictMapper mstDistrictMapper;

    @BeforeEach
    void setUp() {
        mstDistrictMapper = new MstDistrictMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstDistrictSample1();
        var actual = mstDistrictMapper.toEntity(mstDistrictMapper.toDto(expected));
        assertMstDistrictAllPropertiesEquals(expected, actual);
    }
}
