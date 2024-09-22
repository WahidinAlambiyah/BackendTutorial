package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Region;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Region}, with proper type conversions.
 */
@Service
public class RegionRowMapper implements BiFunction<Row, String, Region> {

    private final ColumnConverter converter;

    public RegionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Region} stored in the database.
     */
    @Override
    public Region apply(Row row, String prefix) {
        Region entity = new Region();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUnm49Code(converter.fromRow(row, prefix + "_unm_49_code", String.class));
        entity.setIsoAlpha2Code(converter.fromRow(row, prefix + "_iso_alpha_2_code", String.class));
        return entity;
    }
}
