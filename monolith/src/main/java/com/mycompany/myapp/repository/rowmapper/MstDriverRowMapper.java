package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstDriver;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstDriver}, with proper type conversions.
 */
@Service
public class MstDriverRowMapper implements BiFunction<Row, String, MstDriver> {

    private final ColumnConverter converter;

    public MstDriverRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstDriver} stored in the database.
     */
    @Override
    public MstDriver apply(Row row, String prefix) {
        MstDriver entity = new MstDriver();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setContactNumber(converter.fromRow(row, prefix + "_contact_number", String.class));
        entity.setVehicleDetails(converter.fromRow(row, prefix + "_vehicle_details", String.class));
        return entity;
    }
}
