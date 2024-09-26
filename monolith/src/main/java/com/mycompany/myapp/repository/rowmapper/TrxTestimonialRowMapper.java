package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxTestimonial;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxTestimonial}, with proper type conversions.
 */
@Service
public class TrxTestimonialRowMapper implements BiFunction<Row, String, TrxTestimonial> {

    private final ColumnConverter converter;

    public TrxTestimonialRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxTestimonial} stored in the database.
     */
    @Override
    public TrxTestimonial apply(Row row, String prefix) {
        TrxTestimonial entity = new TrxTestimonial();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setFeedback(converter.fromRow(row, prefix + "_feedback", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Integer.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        return entity;
    }
}
