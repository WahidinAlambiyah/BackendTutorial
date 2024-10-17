package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.domain.enumeration.OrderStatus;
import com.mycompany.myapp.domain.enumeration.PaymentMethod;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxOrder}, with proper type conversions.
 */
@Service
public class TrxOrderRowMapper implements BiFunction<Row, String, TrxOrder> {

    private final ColumnConverter converter;

    public TrxOrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxOrder} stored in the database.
     */
    @Override
    public TrxOrder apply(Row row, String prefix) {
        TrxOrder entity = new TrxOrder();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOrderDate(converter.fromRow(row, prefix + "_order_date", Instant.class));
        entity.setDeliveryDate(converter.fromRow(row, prefix + "_delivery_date", Instant.class));
        entity.setOrderStatus(converter.fromRow(row, prefix + "_order_status", OrderStatus.class));
        entity.setPaymentMethod(converter.fromRow(row, prefix + "_payment_method", PaymentMethod.class));
        entity.setTotalAmount(converter.fromRow(row, prefix + "_total_amount", BigDecimal.class));
        entity.setMstCustomerId(converter.fromRow(row, prefix + "_mst_customer_id", Long.class));
        return entity;
    }
}
