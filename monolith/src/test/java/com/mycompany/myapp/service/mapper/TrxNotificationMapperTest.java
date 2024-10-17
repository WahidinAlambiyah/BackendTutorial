package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxNotificationAsserts.*;
import static com.mycompany.myapp.domain.TrxNotificationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxNotificationMapperTest {

    private TrxNotificationMapper trxNotificationMapper;

    @BeforeEach
    void setUp() {
        trxNotificationMapper = new TrxNotificationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxNotificationSample1();
        var actual = trxNotificationMapper.toEntity(trxNotificationMapper.toDto(expected));
        assertTrxNotificationAllPropertiesEquals(expected, actual);
    }
}
