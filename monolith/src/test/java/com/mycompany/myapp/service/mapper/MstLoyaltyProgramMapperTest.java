package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MstLoyaltyProgramAsserts.*;
import static com.mycompany.myapp.domain.MstLoyaltyProgramTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MstLoyaltyProgramMapperTest {

    private MstLoyaltyProgramMapper mstLoyaltyProgramMapper;

    @BeforeEach
    void setUp() {
        mstLoyaltyProgramMapper = new MstLoyaltyProgramMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMstLoyaltyProgramSample1();
        var actual = mstLoyaltyProgramMapper.toEntity(mstLoyaltyProgramMapper.toDto(expected));
        assertMstLoyaltyProgramAllPropertiesEquals(expected, actual);
    }
}
