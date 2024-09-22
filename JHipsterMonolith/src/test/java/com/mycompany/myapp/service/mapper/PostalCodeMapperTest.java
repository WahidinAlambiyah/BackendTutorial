package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PostalCodeAsserts.*;
import static com.mycompany.myapp.domain.PostalCodeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostalCodeMapperTest {

    private PostalCodeMapper postalCodeMapper;

    @BeforeEach
    void setUp() {
        postalCodeMapper = new PostalCodeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPostalCodeSample1();
        var actual = postalCodeMapper.toEntity(postalCodeMapper.toDto(expected));
        assertPostalCodeAllPropertiesEquals(expected, actual);
    }
}
