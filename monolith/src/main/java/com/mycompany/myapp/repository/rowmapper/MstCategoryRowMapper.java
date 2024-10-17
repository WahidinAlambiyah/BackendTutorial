package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstCategory;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstCategory}, with proper type conversions.
 */
@Service
public class MstCategoryRowMapper implements BiFunction<Row, String, MstCategory> {

    private final ColumnConverter converter;

    public MstCategoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstCategory} stored in the database.
     */
    @Override
    public MstCategory apply(Row row, String prefix) {
        MstCategory entity = new MstCategory();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
