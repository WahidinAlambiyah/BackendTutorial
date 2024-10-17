package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstSupplier;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstSupplier}, with proper type conversions.
 */
@Service
public class MstSupplierRowMapper implements BiFunction<Row, String, MstSupplier> {

    private final ColumnConverter converter;

    public MstSupplierRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstSupplier} stored in the database.
     */
    @Override
    public MstSupplier apply(Row row, String prefix) {
        MstSupplier entity = new MstSupplier();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setContactInfo(converter.fromRow(row, prefix + "_contact_info", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Integer.class));
        return entity;
    }
}
