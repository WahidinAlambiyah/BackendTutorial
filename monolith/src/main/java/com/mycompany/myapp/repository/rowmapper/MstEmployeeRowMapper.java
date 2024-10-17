package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstEmployee;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstEmployee}, with proper type conversions.
 */
@Service
public class MstEmployeeRowMapper implements BiFunction<Row, String, MstEmployee> {

    private final ColumnConverter converter;

    public MstEmployeeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstEmployee} stored in the database.
     */
    @Override
    public MstEmployee apply(Row row, String prefix) {
        MstEmployee entity = new MstEmployee();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        entity.setHireDate(converter.fromRow(row, prefix + "_hire_date", Instant.class));
        entity.setSalary(converter.fromRow(row, prefix + "_salary", Long.class));
        entity.setCommissionPct(converter.fromRow(row, prefix + "_commission_pct", Long.class));
        entity.setManagerId(converter.fromRow(row, prefix + "_manager_id", Long.class));
        entity.setDepartmentId(converter.fromRow(row, prefix + "_department_id", Long.class));
        entity.setMstDepartmentId(converter.fromRow(row, prefix + "_mst_department_id", Long.class));
        return entity;
    }
}
