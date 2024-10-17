package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TrxOrderHistorySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("previous_status", table, columnPrefix + "_previous_status"));
        columns.add(Column.aliased("new_status", table, columnPrefix + "_new_status"));
        columns.add(Column.aliased("change_date", table, columnPrefix + "_change_date"));

        return columns;
    }
}
