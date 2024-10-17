package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxCart;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxCart}, with proper type conversions.
 */
@Service
public class TrxCartRowMapper implements BiFunction<Row, String, TrxCart> {

    private final ColumnConverter converter;

    public TrxCartRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxCart} stored in the database.
     */
    @Override
    public TrxCart apply(Row row, String prefix) {
        TrxCart entity = new TrxCart();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
