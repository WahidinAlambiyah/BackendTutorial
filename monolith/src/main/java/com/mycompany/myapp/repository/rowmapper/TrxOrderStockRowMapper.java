package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxOrderStock;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxOrderStock}, with proper type conversions.
 */
@Service
public class TrxOrderStockRowMapper implements BiFunction<Row, String, TrxOrderStock> {

    private final ColumnConverter converter;

    public TrxOrderStockRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxOrderStock} stored in the database.
     */
    @Override
    public TrxOrderStock apply(Row row, String prefix) {
        TrxOrderStock entity = new TrxOrderStock();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantityOrdered(converter.fromRow(row, prefix + "_quantity_ordered", Integer.class));
        entity.setOrderDate(converter.fromRow(row, prefix + "_order_date", Instant.class));
        entity.setExpectedArrivalDate(converter.fromRow(row, prefix + "_expected_arrival_date", Instant.class));
        entity.setSupplierId(converter.fromRow(row, prefix + "_supplier_id", Long.class));
        return entity;
    }
}
