package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TrxCouponSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("discount_amount", table, columnPrefix + "_discount_amount"));
        columns.add(Column.aliased("valid_until", table, columnPrefix + "_valid_until"));
        columns.add(Column.aliased("min_purchase", table, columnPrefix + "_min_purchase"));

        return columns;
    }
}
