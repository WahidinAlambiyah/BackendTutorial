package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TrxDeliverySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("delivery_address", table, columnPrefix + "_delivery_address"));
        columns.add(Column.aliased("delivery_status", table, columnPrefix + "_delivery_status"));
        columns.add(Column.aliased("assigned_driver", table, columnPrefix + "_assigned_driver"));
        columns.add(Column.aliased("estimated_delivery_time", table, columnPrefix + "_estimated_delivery_time"));

        columns.add(Column.aliased("driver_id", table, columnPrefix + "_driver_id"));
        columns.add(Column.aliased("trx_order_id", table, columnPrefix + "_trx_order_id"));
        return columns;
    }
}
