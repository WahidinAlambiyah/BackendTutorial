package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstCategoryAsserts.*;
import static com.mycompany.myapp.domain.MstCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstCategoryMapperTest {

    private MstCategoryMapper mstCategoryMapper;

    @BeforeEach
    void setUp() {
        mstCategoryMapper = new MstCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstCategorySample1();
        var actual = mstCategoryMapper.toEntity(mstCategoryMapper.toDto(expected));
        assertMstCategoryAllPropertiesEquals(expected, actual);
    }
}
