package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstSubDistrictAsserts.*;
import static com.mycompany.myapp.domain.MstSubDistrictTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstSubDistrictMapperTest {

    private MstSubDistrictMapper mstSubDistrictMapper;

    @BeforeEach
    void setUp() {
        mstSubDistrictMapper = new MstSubDistrictMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstSubDistrictSample1();
        var actual = mstSubDistrictMapper.toEntity(mstSubDistrictMapper.toDto(expected));
        assertMstSubDistrictAllPropertiesEquals(expected, actual);
    }
}
