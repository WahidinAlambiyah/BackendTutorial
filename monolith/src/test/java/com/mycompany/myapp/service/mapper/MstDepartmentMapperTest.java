package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstDepartmentAsserts.*;
import static com.mycompany.myapp.domain.MstDepartmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstDepartmentMapperTest {

    private MstDepartmentMapper mstDepartmentMapper;

    @BeforeEach
    void setUp() {
        mstDepartmentMapper = new MstDepartmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstDepartmentSample1();
        var actual = mstDepartmentMapper.toEntity(mstDepartmentMapper.toDto(expected));
        assertMstDepartmentAllPropertiesEquals(expected, actual);
    }
}
