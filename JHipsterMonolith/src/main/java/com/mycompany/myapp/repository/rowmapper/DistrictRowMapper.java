package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.District;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link District}, with proper type conversions.
 */
@Service
public class DistrictRowMapper implements BiFunction<Row, String, District> {

    private final ColumnConverter converter;

    public DistrictRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link District} stored in the database.
     */
    @Override
    public District apply(Row row, String prefix) {
        District entity = new District();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUnm49Code(converter.fromRow(row, prefix + "_unm_49_code", String.class));
        entity.setIsoAlpha2Code(converter.fromRow(row, prefix + "_iso_alpha_2_code", String.class));
        entity.setCityId(converter.fromRow(row, prefix + "_city_id", Long.class));
        return entity;
    }
}
