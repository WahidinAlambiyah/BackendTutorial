package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstCustomerAsserts.*;
import static com.mycompany.myapp.domain.MstCustomerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstCustomerMapperTest {

    private MstCustomerMapper mstCustomerMapper;

    @BeforeEach
    void setUp() {
        mstCustomerMapper = new MstCustomerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstCustomerSample1();
        var actual = mstCustomerMapper.toEntity(mstCustomerMapper.toDto(expected));
        assertMstCustomerAllPropertiesEquals(expected, actual);
    }
}
