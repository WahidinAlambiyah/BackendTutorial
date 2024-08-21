package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Province;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Province}, with proper type conversions.
 */
@Service
public class ProvinceRowMapper implements BiFunction<Row, String, Province> {

    private final ColumnConverter converter;

    public ProvinceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Province} stored in the database.
     */
    @Override
    public Province apply(Row row, String prefix) {
        Province entity = new Province();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setCountryId(converter.fromRow(row, prefix + "_country_id", Long.class));
        return entity;
    }
}
