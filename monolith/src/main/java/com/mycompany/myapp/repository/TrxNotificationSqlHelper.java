package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TrxNotificationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("recipient", table, columnPrefix + "_recipient"));
        columns.add(Column.aliased("message_type", table, columnPrefix + "_message_type"));
        columns.add(Column.aliased("content", table, columnPrefix + "_content"));
        columns.add(Column.aliased("sent_at", table, columnPrefix + "_sent_at"));

        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        return columns;
    }
}
