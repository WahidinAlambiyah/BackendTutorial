package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxStockAlert} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxStockAlertResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-stock-alerts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxStockAlertCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter alertThreshold;

    private IntegerFilter currentStock;

    private Boolean distinct;

    public TrxStockAlertCriteria() {}

    public TrxStockAlertCriteria(TrxStockAlertCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.alertThreshold = other.optionalAlertThreshold().map(IntegerFilter::copy).orElse(null);
        this.currentStock = other.optionalCurrentStock().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxStockAlertCriteria copy() {
        return new TrxStockAlertCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getAlertThreshold() {
        return alertThreshold;
    }

    public Optional<IntegerFilter> optionalAlertThreshold() {
        return Optional.ofNullable(alertThreshold);
    }

    public IntegerFilter alertThreshold() {
        if (alertThreshold == null) {
            setAlertThreshold(new IntegerFilter());
        }
        return alertThreshold;
    }

    public void setAlertThreshold(IntegerFilter alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public IntegerFilter getCurrentStock() {
        return currentStock;
    }

    public Optional<IntegerFilter> optionalCurrentStock() {
        return Optional.ofNullable(currentStock);
    }

    public IntegerFilter currentStock() {
        if (currentStock == null) {
            setCurrentStock(new IntegerFilter());
        }
        return currentStock;
    }

    public void setCurrentStock(IntegerFilter currentStock) {
        this.currentStock = currentStock;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TrxStockAlertCriteria that = (TrxStockAlertCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(alertThreshold, that.alertThreshold) &&
            Objects.equals(currentStock, that.currentStock) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alertThreshold, currentStock, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxStockAlertCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAlertThreshold().map(f -> "alertThreshold=" + f + ", ").orElse("") +
            optionalCurrentStock().map(f -> "currentStock=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
