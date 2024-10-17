package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxDiscount;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxDiscount}, with proper type conversions.
 */
@Service
public class TrxDiscountRowMapper implements BiFunction<Row, String, TrxDiscount> {

    private final ColumnConverter converter;

    public TrxDiscountRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxDiscount} stored in the database.
     */
    @Override
    public TrxDiscount apply(Row row, String prefix) {
        TrxDiscount entity = new TrxDiscount();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDiscountPercentage(converter.fromRow(row, prefix + "_discount_percentage", Float.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", Instant.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", Instant.class));
        return entity;
    }
}
