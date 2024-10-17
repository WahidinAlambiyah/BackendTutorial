package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstSupplierAsserts.*;
import static com.mycompany.myapp.domain.MstSupplierTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstSupplierMapperTest {

    private MstSupplierMapper mstSupplierMapper;

    @BeforeEach
    void setUp() {
        mstSupplierMapper = new MstSupplierMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstSupplierSample1();
        var actual = mstSupplierMapper.toEntity(mstSupplierMapper.toDto(expected));
        assertMstSupplierAllPropertiesEquals(expected, actual);
    }
}
