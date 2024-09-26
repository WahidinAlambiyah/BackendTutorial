package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstCity;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstCity}, with proper type conversions.
 */
@Service
public class MstCityRowMapper implements BiFunction<Row, String, MstCity> {

    private final ColumnConverter converter;

    public MstCityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstCity} stored in the database.
     */
    @Override
    public MstCity apply(Row row, String prefix) {
        MstCity entity = new MstCity();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUnm49Code(converter.fromRow(row, prefix + "_unm_49_code", String.class));
        entity.setIsoAlpha2Code(converter.fromRow(row, prefix + "_iso_alpha_2_code", String.class));
        entity.setProvinceId(converter.fromRow(row, prefix + "_province_id", Long.class));
        return entity;
    }
}
