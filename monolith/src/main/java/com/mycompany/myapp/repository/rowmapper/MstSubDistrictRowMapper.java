package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstSubDistrict;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstSubDistrict}, with proper type conversions.
 */
@Service
public class MstSubDistrictRowMapper implements BiFunction<Row, String, MstSubDistrict> {

    private final ColumnConverter converter;

    public MstSubDistrictRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstSubDistrict} stored in the database.
     */
    @Override
    public MstSubDistrict apply(Row row, String prefix) {
        MstSubDistrict entity = new MstSubDistrict();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUnm49Code(converter.fromRow(row, prefix + "_unm_49_code", String.class));
        entity.setIsoAlpha2Code(converter.fromRow(row, prefix + "_iso_alpha_2_code", String.class));
        entity.setDistrictId(converter.fromRow(row, prefix + "_district_id", Long.class));
        return entity;
    }
}
