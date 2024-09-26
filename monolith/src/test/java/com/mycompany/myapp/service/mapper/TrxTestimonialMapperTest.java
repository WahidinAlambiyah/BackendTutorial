package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxTestimonialAsserts.*;
import static com.mycompany.myapp.domain.TrxTestimonialTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxTestimonialMapperTest {

    private TrxTestimonialMapper trxTestimonialMapper;

    @BeforeEach
    void setUp() {
        trxTestimonialMapper = new TrxTestimonialMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxTestimonialSample1();
        var actual = trxTestimonialMapper.toEntity(trxTestimonialMapper.toDto(expected));
        assertTrxTestimonialAllPropertiesEquals(expected, actual);
    }
}
