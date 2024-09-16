package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Tournament;
import com.mycompany.myapp.domain.enumeration.TournamentStatus;
import com.mycompany.myapp.domain.enumeration.TournamentType;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tournament}, with proper type conversions.
 */
@Service
public class TournamentRowMapper implements BiFunction<Row, String, Tournament> {

    private final ColumnConverter converter;

    public TournamentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tournament} stored in the database.
     */
    @Override
    public Tournament apply(Row row, String prefix) {
        Tournament entity = new Tournament();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", TournamentType.class));
        entity.setPrizeAmount(converter.fromRow(row, prefix + "_prize_amount", BigDecimal.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", Instant.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", Instant.class));
        entity.setLocation(converter.fromRow(row, prefix + "_location", String.class));
        entity.setMaxParticipants(converter.fromRow(row, prefix + "_max_participants", Integer.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", TournamentStatus.class));
        entity.setEventId(converter.fromRow(row, prefix + "_event_id", Long.class));
        return entity;
    }
}
