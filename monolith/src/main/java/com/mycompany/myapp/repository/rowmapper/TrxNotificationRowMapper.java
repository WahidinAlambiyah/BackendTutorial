package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxNotification;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxNotification}, with proper type conversions.
 */
@Service
public class TrxNotificationRowMapper implements BiFunction<Row, String, TrxNotification> {

    private final ColumnConverter converter;

    public TrxNotificationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxNotification} stored in the database.
     */
    @Override
    public TrxNotification apply(Row row, String prefix) {
        TrxNotification entity = new TrxNotification();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRecipient(converter.fromRow(row, prefix + "_recipient", String.class));
        entity.setMessageType(converter.fromRow(row, prefix + "_message_type", String.class));
        entity.setContent(converter.fromRow(row, prefix + "_content", String.class));
        entity.setSentAt(converter.fromRow(row, prefix + "_sent_at", Instant.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
