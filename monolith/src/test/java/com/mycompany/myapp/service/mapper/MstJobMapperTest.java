package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstJobAsserts.*;
import static com.mycompany.myapp.domain.MstJobTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstJobMapperTest {

    private MstJobMapper mstJobMapper;

    @BeforeEach
    void setUp() {
        mstJobMapper = new MstJobMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstJobSample1();
        var actual = mstJobMapper.toEntity(mstJobMapper.toDto(expected));
        assertMstJobAllPropertiesEquals(expected, actual);
    }
}
