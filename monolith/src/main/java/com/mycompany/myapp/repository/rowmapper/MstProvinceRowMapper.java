package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstProvince;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstProvince}, with proper type conversions.
 */
@Service
public class MstProvinceRowMapper implements BiFunction<Row, String, MstProvince> {

    private final ColumnConverter converter;

    public MstProvinceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstProvince} stored in the database.
     */
    @Override
    public MstProvince apply(Row row, String prefix) {
        MstProvince entity = new MstProvince();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUnm49Code(converter.fromRow(row, prefix + "_unm_49_code", String.class));
        entity.setIsoAlpha2Code(converter.fromRow(row, prefix + "_iso_alpha_2_code", String.class));
        entity.setCountryId(converter.fromRow(row, prefix + "_country_id", Long.class));
        return entity;
    }
}
