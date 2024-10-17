package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstBrand;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstBrand}, with proper type conversions.
 */
@Service
public class MstBrandRowMapper implements BiFunction<Row, String, MstBrand> {

    private final ColumnConverter converter;

    public MstBrandRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstBrand} stored in the database.
     */
    @Override
    public MstBrand apply(Row row, String prefix) {
        MstBrand entity = new MstBrand();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setLogo(converter.fromRow(row, prefix + "_logo", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
