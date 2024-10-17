package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.MstLoyaltyProgram;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MstLoyaltyProgram}, with proper type conversions.
 */
@Service
public class MstLoyaltyProgramRowMapper implements BiFunction<Row, String, MstLoyaltyProgram> {

    private final ColumnConverter converter;

    public MstLoyaltyProgramRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MstLoyaltyProgram} stored in the database.
     */
    @Override
    public MstLoyaltyProgram apply(Row row, String prefix) {
        MstLoyaltyProgram entity = new MstLoyaltyProgram();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPointsEarned(converter.fromRow(row, prefix + "_points_earned", Integer.class));
        entity.setMembershipTier(converter.fromRow(row, prefix + "_membership_tier", String.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
