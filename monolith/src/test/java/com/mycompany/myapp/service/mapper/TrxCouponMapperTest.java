package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxCouponAsserts.*;
import static com.mycompany.myapp.domain.TrxCouponTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxCouponMapperTest {

    private TrxCouponMapper trxCouponMapper;

    @BeforeEach
    void setUp() {
        trxCouponMapper = new TrxCouponMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxCouponSample1();
        var actual = trxCouponMapper.toEntity(trxCouponMapper.toDto(expected));
        assertTrxCouponAllPropertiesEquals(expected, actual);
    }
}
