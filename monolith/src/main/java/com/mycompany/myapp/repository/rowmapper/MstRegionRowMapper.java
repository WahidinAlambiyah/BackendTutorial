package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstRegion;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstRegion}, with proper type conversions.
 */
@Service
public class MstRegionRowMapper implements BiFunction<Row, String, MstRegion> {

    private final ColumnConverter converter;

    public MstRegionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstRegion} stored in the database.
     */
    @Override
    public MstRegion apply(Row row, String prefix) {
        MstRegion entity = new MstRegion();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUnm49Code(converter.fromRow(row, prefix + "_unm_49_code", String.class));
        entity.setIsoAlpha2Code(converter.fromRow(row, prefix + "_iso_alpha_2_code", String.class));
        return entity;
    }
}
