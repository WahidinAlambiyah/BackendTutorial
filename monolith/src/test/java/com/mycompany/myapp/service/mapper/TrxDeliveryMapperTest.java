package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxDeliveryAsserts.*;
import static com.mycompany.myapp.domain.TrxDeliveryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxDeliveryMapperTest {

    private TrxDeliveryMapper trxDeliveryMapper;

    @BeforeEach
    void setUp() {
        trxDeliveryMapper = new TrxDeliveryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxDeliverySample1();
        var actual = trxDeliveryMapper.toEntity(trxDeliveryMapper.toDto(expected));
        assertTrxDeliveryAllPropertiesEquals(expected, actual);
    }
}
