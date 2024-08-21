package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.SubDistrict;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link SubDistrict}, with proper type conversions.
 */
@Service
public class SubDistrictRowMapper implements BiFunction<Row, String, SubDistrict> {

    private final ColumnConverter converter;

    public SubDistrictRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link SubDistrict} stored in the database.
     */
    @Override
    public SubDistrict apply(Row row, String prefix) {
        SubDistrict entity = new SubDistrict();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDistrictId(converter.fromRow(row, prefix + "_district_id", Long.class));
        return entity;
    }
}
