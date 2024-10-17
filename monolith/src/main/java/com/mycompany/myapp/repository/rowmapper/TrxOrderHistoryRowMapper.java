package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxOrderHistory;
import com.mycompany.myapp.domain.enumeration.OrderStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxOrderHistory}, with proper type conversions.
 */
@Service
public class TrxOrderHistoryRowMapper implements BiFunction<Row, String, TrxOrderHistory> {

    private final ColumnConverter converter;

    public TrxOrderHistoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxOrderHistory} stored in the database.
     */
    @Override
    public TrxOrderHistory apply(Row row, String prefix) {
        TrxOrderHistory entity = new TrxOrderHistory();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPreviousStatus(converter.fromRow(row, prefix + "_previous_status", OrderStatus.class));
        entity.setNewStatus(converter.fromRow(row, prefix + "_new_status", OrderStatus.class));
        entity.setChangeDate(converter.fromRow(row, prefix + "_change_date", Instant.class));
        return entity;
    }
}
