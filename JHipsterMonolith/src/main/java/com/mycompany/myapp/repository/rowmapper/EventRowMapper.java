package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Event;
import com.mycompany.myapp.domain.enumeration.EventStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Event}, with proper type conversions.
 */
@Service
public class EventRowMapper implements BiFunction<Row, String, Event> {

    private final ColumnConverter converter;

    public EventRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Event} stored in the database.
     */
    @Override
    public Event apply(Row row, String prefix) {
        Event entity = new Event();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setLocation(converter.fromRow(row, prefix + "_location", String.class));
        entity.setCapacity(converter.fromRow(row, prefix + "_capacity", Integer.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", EventStatus.class));
        entity.setServiceId(converter.fromRow(row, prefix + "_service_id", Long.class));
        entity.setTestimonialId(converter.fromRow(row, prefix + "_testimonial_id", Long.class));
        return entity;
    }
}
