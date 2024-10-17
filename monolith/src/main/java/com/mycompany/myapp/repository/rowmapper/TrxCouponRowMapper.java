package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxCoupon;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxCoupon}, with proper type conversions.
 */
@Service
public class TrxCouponRowMapper implements BiFunction<Row, String, TrxCoupon> {

    private final ColumnConverter converter;

    public TrxCouponRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxCoupon} stored in the database.
     */
    @Override
    public TrxCoupon apply(Row row, String prefix) {
        TrxCoupon entity = new TrxCoupon();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDiscountAmount(converter.fromRow(row, prefix + "_discount_amount", BigDecimal.class));
        entity.setValidUntil(converter.fromRow(row, prefix + "_valid_until", Instant.class));
        entity.setMinPurchase(converter.fromRow(row, prefix + "_min_purchase", BigDecimal.class));
        return entity;
    }
}
