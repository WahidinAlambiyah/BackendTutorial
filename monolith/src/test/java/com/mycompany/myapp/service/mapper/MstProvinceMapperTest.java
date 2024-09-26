package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstProvinceAsserts.*;
import static com.mycompany.myapp.domain.MstProvinceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstProvinceMapperTest {

    private MstProvinceMapper mstProvinceMapper;

    @BeforeEach
    void setUp() {
        mstProvinceMapper = new MstProvinceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstProvinceSample1();
        var actual = mstProvinceMapper.toEntity(mstProvinceMapper.toDto(expected));
        assertMstProvinceAllPropertiesEquals(expected, actual);
    }
}
