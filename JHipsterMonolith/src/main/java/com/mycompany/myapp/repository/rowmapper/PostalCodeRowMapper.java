package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.PostalCode;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PostalCode}, with proper type conversions.
 */
@Service
public class PostalCodeRowMapper implements BiFunction<Row, String, PostalCode> {

    private final ColumnConverter converter;

    public PostalCodeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PostalCode} stored in the database.
     */
    @Override
    public PostalCode apply(Row row, String prefix) {
        PostalCode entity = new PostalCode();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setSubDistrictId(converter.fromRow(row, prefix + "_sub_district_id", Long.class));
        return entity;
    }
}
