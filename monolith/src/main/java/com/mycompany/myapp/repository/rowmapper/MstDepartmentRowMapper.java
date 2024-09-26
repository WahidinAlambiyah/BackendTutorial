package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstDepartment;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstDepartment}, with proper type conversions.
 */
@Service
public class MstDepartmentRowMapper implements BiFunction<Row, String, MstDepartment> {

    private final ColumnConverter converter;

    public MstDepartmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstDepartment} stored in the database.
     */
    @Override
    public MstDepartment apply(Row row, String prefix) {
        MstDepartment entity = new MstDepartment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDepartmentName(converter.fromRow(row, prefix + "_department_name", String.class));
        entity.setLocationId(converter.fromRow(row, prefix + "_location_id", Long.class));
        return entity;
    }
}
