package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstTask;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstTask}, with proper type conversions.
 */
@Service
public class MstTaskRowMapper implements BiFunction<Row, String, MstTask> {

    private final ColumnConverter converter;

    public MstTaskRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstTask} stored in the database.
     */
    @Override
    public MstTask apply(Row row, String prefix) {
        MstTask entity = new MstTask();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
