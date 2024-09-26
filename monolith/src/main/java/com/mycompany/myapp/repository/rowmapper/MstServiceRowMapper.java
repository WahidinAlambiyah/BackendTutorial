package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstService;
import com.mycompany.myapp.domain.enumeration.ServiceType;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstService}, with proper type conversions.
 */
@Service
public class MstServiceRowMapper implements BiFunction<Row, String, MstService> {

    private final ColumnConverter converter;

    public MstServiceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstService} stored in the database.
     */
    @Override
    public MstService apply(Row row, String prefix) {
        MstService entity = new MstService();
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
