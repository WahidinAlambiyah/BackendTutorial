package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstPostalCode;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstPostalCode}, with proper type conversions.
 */
@Service
public class MstPostalCodeRowMapper implements BiFunction<Row, String, MstPostalCode> {

    private final ColumnConverter converter;

    public MstPostalCodeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstPostalCode} stored in the database.
     */
    @Override
    public MstPostalCode apply(Row row, String prefix) {
        MstPostalCode entity = new MstPostalCode();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setSubDistrictId(converter.fromRow(row, prefix + "_sub_district_id", Long.class));
        return entity;
    }
}
