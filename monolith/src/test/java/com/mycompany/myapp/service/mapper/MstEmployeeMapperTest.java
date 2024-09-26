package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstEmployeeAsserts.*;
import static com.mycompany.myapp.domain.MstEmployeeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstEmployeeMapperTest {

    private MstEmployeeMapper mstEmployeeMapper;

    @BeforeEach
    void setUp() {
        mstEmployeeMapper = new MstEmployeeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstEmployeeSample1();
        var actual = mstEmployeeMapper.toEntity(mstEmployeeMapper.toDto(expected));
        assertMstEmployeeAllPropertiesEquals(expected, actual);
    }
}
