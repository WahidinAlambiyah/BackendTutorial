package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstCustomer;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstCustomer}, with proper type conversions.
 */
@Service
public class MstCustomerRowMapper implements BiFunction<Row, String, MstCustomer> {

    private final ColumnConverter converter;

    public MstCustomerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstCustomer} stored in the database.
     */
    @Override
    public MstCustomer apply(Row row, String prefix) {
        MstCustomer entity = new MstCustomer();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setLoyaltyPoints(converter.fromRow(row, prefix + "_loyalty_points", Integer.class));
        return entity;
    }
}
