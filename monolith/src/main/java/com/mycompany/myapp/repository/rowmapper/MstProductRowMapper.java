package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstProduct;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstProduct}, with proper type conversions.
 */
@Service
public class MstProductRowMapper implements BiFunction<Row, String, MstProduct> {

    private final ColumnConverter converter;

    public MstProductRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstProduct} stored in the database.
     */
    @Override
    public MstProduct apply(Row row, String prefix) {
        MstProduct entity = new MstProduct();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setBarcode(converter.fromRow(row, prefix + "_barcode", String.class));
        entity.setUnitSize(converter.fromRow(row, prefix + "_unit_size", String.class));
        entity.setCategoryId(converter.fromRow(row, prefix + "_category_id", Long.class));
        entity.setBrandId(converter.fromRow(row, prefix + "_brand_id", Long.class));
        entity.setMstSupplierId(converter.fromRow(row, prefix + "_mst_supplier_id", Long.class));
        return entity;
    }
}
