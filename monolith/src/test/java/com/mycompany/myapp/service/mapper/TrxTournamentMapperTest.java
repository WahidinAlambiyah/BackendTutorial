package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TrxTournamentAsserts.*;
import static com.mycompany.myapp.domain.TrxTournamentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrxTournamentMapperTest {

    private TrxTournamentMapper trxTournamentMapper;

    @BeforeEach
    void setUp() {
        trxTournamentMapper = new TrxTournamentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrxTournamentSample1();
        var actual = trxTournamentMapper.toEntity(trxTournamentMapper.toDto(expected));
        assertTrxTournamentAllPropertiesEquals(expected, actual);
    }
}
