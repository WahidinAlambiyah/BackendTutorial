package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstCountry;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstCountry}, with proper type conversions.
 */
@Service
public class MstCountryRowMapper implements BiFunction<Row, String, MstCountry> {

    private final ColumnConverter converter;

    public MstCountryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstCountry} stored in the database.
     */
    @Override
    public MstCountry apply(Row row, String prefix) {
        MstCountry entity = new MstCountry();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUnm49Code(converter.fromRow(row, prefix + "_unm_49_code", String.class));
        entity.setIsoAlpha2Code(converter.fromRow(row, prefix + "_iso_alpha_2_code", String.class));
        entity.setRegionId(converter.fromRow(row, prefix + "_region_id", Long.class));
        return entity;
    }
}
