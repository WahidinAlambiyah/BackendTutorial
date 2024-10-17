package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxOrderItem;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxOrderItem}, with proper type conversions.
 */
@Service
public class TrxOrderItemRowMapper implements BiFunction<Row, String, TrxOrderItem> {

    private final ColumnConverter converter;

    public TrxOrderItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxOrderItem} stored in the database.
     */
    @Override
    public TrxOrderItem apply(Row row, String prefix) {
        TrxOrderItem entity = new TrxOrderItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
