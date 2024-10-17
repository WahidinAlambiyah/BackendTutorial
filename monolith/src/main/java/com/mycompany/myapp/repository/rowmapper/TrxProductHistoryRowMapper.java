package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrxProductHistory;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrxProductHistory}, with proper type conversions.
 */
@Service
public class TrxProductHistoryRowMapper implements BiFunction<Row, String, TrxProductHistory> {

    private final ColumnConverter converter;

    public TrxProductHistoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrxProductHistory} stored in the database.
     */
    @Override
    public TrxProductHistory apply(Row row, String prefix) {
        TrxProductHistory entity = new TrxProductHistory();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOldPrice(converter.fromRow(row, prefix + "_old_price", BigDecimal.class));
        entity.setNewPrice(converter.fromRow(row, prefix + "_new_price", BigDecimal.class));
        entity.setChangeDate(converter.fromRow(row, prefix + "_change_date", Instant.class));
        return entity;
    }
}
