package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Services;
import com.mycompany.myapp.domain.enumeration.ServiceType;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Services}, with proper type conversions.
 */
@Service
public class ServiceRowMapper implements BiFunction<Row, String, Services> {

    private final ColumnConverter converter;

    public ServiceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Services} stored in the database.
     */
    @Override
    public Services apply(Row row, String prefix) {
        Services entity = new Services();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setDurationInHours(converter.fromRow(row, prefix + "_duration_in_hours", Integer.class));
        entity.setServiceType(converter.fromRow(row, prefix + "_service_type", ServiceType.class));
        entity.setTestimonialId(converter.fromRow(row, prefix + "_testimonial_id", Long.class));
        return entity;
    }
}
