package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TrxProductHistorySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("old_price", table, columnPrefix + "_old_price"));
        columns.add(Column.aliased("new_price", table, columnPrefix + "_new_price"));
        columns.add(Column.aliased("change_date", table, columnPrefix + "_change_date"));

        return columns;
    }
}
