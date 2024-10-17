package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TrxOrderStockSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("quantity_ordered", table, columnPrefix + "_quantity_ordered"));
        columns.add(Column.aliased("order_date", table, columnPrefix + "_order_date"));
        columns.add(Column.aliased("expected_arrival_date", table, columnPrefix + "_expected_arrival_date"));

        columns.add(Column.aliased("supplier_id", table, columnPrefix + "_supplier_id"));
        return columns;
    }
}
