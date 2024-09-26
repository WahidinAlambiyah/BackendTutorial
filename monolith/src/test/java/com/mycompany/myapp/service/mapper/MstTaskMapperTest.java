package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstTaskAsserts.*;
import static com.mycompany.myapp.domain.MstTaskTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstTaskMapperTest {

    private MstTaskMapper mstTaskMapper;

    @BeforeEach
    void setUp() {
        mstTaskMapper = new MstTaskMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstTaskSample1();
        var actual = mstTaskMapper.toEntity(mstTaskMapper.toDto(expected));
        assertMstTaskAllPropertiesEquals(expected, actual);
    }
}
