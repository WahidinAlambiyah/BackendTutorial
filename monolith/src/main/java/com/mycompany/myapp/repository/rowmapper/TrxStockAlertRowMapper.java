package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxStockAlert;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxStockAlert}, with proper type conversions.
 */
@Service
public class TrxStockAlertRowMapper implements BiFunction<Row, String, TrxStockAlert> {

    private final ColumnConverter converter;

    public TrxStockAlertRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxStockAlert} stored in the database.
     */
    @Override
    public TrxStockAlert apply(Row row, String prefix) {
        TrxStockAlert entity = new TrxStockAlert();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAlertThreshold(converter.fromRow(row, prefix + "_alert_threshold", Integer.class));
        entity.setCurrentStock(converter.fromRow(row, prefix + "_current_stock", Integer.class));
        return entity;
    }
}
