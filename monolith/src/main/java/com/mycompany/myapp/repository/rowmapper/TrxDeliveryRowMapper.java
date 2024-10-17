package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxDelivery;
import com.mycompany.myapp.domain.enumeration.DeliveryStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxDelivery}, with proper type conversions.
 */
@Service
public class TrxDeliveryRowMapper implements BiFunction<Row, String, TrxDelivery> {

    private final ColumnConverter converter;

    public TrxDeliveryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxDelivery} stored in the database.
     */
    @Override
    public TrxDelivery apply(Row row, String prefix) {
        TrxDelivery entity = new TrxDelivery();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDeliveryAddress(converter.fromRow(row, prefix + "_delivery_address", String.class));
        entity.setDeliveryStatus(converter.fromRow(row, prefix + "_delivery_status", DeliveryStatus.class));
        entity.setAssignedDriver(converter.fromRow(row, prefix + "_assigned_driver", String.class));
        entity.setEstimatedDeliveryTime(converter.fromRow(row, prefix + "_estimated_delivery_time", Instant.class));
        entity.setDriverId(converter.fromRow(row, prefix + "_driver_id", Long.class));
        entity.setTrxOrderId(converter.fromRow(row, prefix + "_trx_order_id", Long.class));
        return entity;
    }
}
