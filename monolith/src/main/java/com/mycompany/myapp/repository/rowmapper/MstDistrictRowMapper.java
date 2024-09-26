package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstDistrict;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstDistrict}, with proper type conversions.
 */
@Service
public class MstDistrictRowMapper implements BiFunction<Row, String, MstDistrict> {

    private final ColumnConverter converter;

    public MstDistrictRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstDistrict} stored in the database.
     */
    @Override
    public MstDistrict apply(Row row, String prefix) {
        MstDistrict entity = new MstDistrict();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUnm49Code(converter.fromRow(row, prefix + "_unm_49_code", String.class));
        entity.setIsoAlpha2Code(converter.fromRow(row, prefix + "_iso_alpha_2_code", String.class));
        entity.setCityId(converter.fromRow(row, prefix + "_city_id", Long.class));
        return entity;
    }
}
