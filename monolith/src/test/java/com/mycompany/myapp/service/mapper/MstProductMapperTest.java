package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstProductAsserts.*;
import static com.mycompany.myapp.domain.MstProductTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstProductMapperTest {

    private MstProductMapper mstProductMapper;

    @BeforeEach
    void setUp() {
        mstProductMapper = new MstProductMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstProductSample1();
        var actual = mstProductMapper.toEntity(mstProductMapper.toDto(expected));
        assertMstProductAllPropertiesEquals(expected, actual);
    }
}
