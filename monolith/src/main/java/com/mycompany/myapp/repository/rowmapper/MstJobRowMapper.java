package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstJob;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstJob}, with proper type conversions.
 */
@Service
public class MstJobRowMapper implements BiFunction<Row, String, MstJob> {

    private final ColumnConverter converter;

    public MstJobRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstJob} stored in the database.
     */
    @Override
    public MstJob apply(Row row, String prefix) {
        MstJob entity = new MstJob();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setJobTitle(converter.fromRow(row, prefix + "_job_title", String.class));
        entity.setMinSalary(converter.fromRow(row, prefix + "_min_salary", Long.class));
        entity.setMaxSalary(converter.fromRow(row, prefix + "_max_salary", Long.class));
        entity.setEmployeeId(converter.fromRow(row, prefix + "_employee_id", Long.class));
        return entity;
    }
}
