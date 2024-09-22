package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SubDistrictAsserts.*;
import static com.mycompany.myapp.domain.SubDistrictTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubDistrictMapperTest {

    private SubDistrictMapper subDistrictMapper;

    @BeforeEach
    void setUp() {
        subDistrictMapper = new SubDistrictMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSubDistrictSample1();
        var actual = subDistrictMapper.toEntity(subDistrictMapper.toDto(expected));
        assertSubDistrictAllPropertiesEquals(expected, actual);
    }
}
