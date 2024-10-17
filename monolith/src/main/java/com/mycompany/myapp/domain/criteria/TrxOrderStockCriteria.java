package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxOrderStock} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxOrderStockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-order-stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderStockCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter quantityOrdered;

    private InstantFilter orderDate;

    private InstantFilter expectedArrivalDate;

    private LongFilter supplierId;

    private Boolean distinct;

    public TrxOrderStockCriteria() {}

    public TrxOrderStockCriteria(TrxOrderStockCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantityOrdered = other.optionalQuantityOrdered().map(IntegerFilter::copy).orElse(null);
        this.orderDate = other.optionalOrderDate().map(InstantFilter::copy).orElse(null);
        this.expectedArrivalDate = other.optionalExpectedArrivalDate().map(InstantFilter::copy).orElse(null);
        this.supplierId = other.optionalSupplierId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxOrderStockCriteria copy() {
        return new TrxOrderStockCriteria(this);
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

    public IntegerFilter getQuantityOrdered() {
        return quantityOrdered;
    }

    public Optional<IntegerFilter> optionalQuantityOrdered() {
        return Optional.ofNullable(quantityOrdered);
    }

    public IntegerFilter quantityOrdered() {
        if (quantityOrdered == null) {
            setQuantityOrdered(new IntegerFilter());
        }
        return quantityOrdered;
    }

    public void setQuantityOrdered(IntegerFilter quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public InstantFilter getOrderDate() {
        return orderDate;
    }

    public Optional<InstantFilter> optionalOrderDate() {
        return Optional.ofNullable(orderDate);
    }

    public InstantFilter orderDate() {
        if (orderDate == null) {
            setOrderDate(new InstantFilter());
        }
        return orderDate;
    }

    public void setOrderDate(InstantFilter orderDate) {
        this.orderDate = orderDate;
    }

    public InstantFilter getExpectedArrivalDate() {
        return expectedArrivalDate;
    }

    public Optional<InstantFilter> optionalExpectedArrivalDate() {
        return Optional.ofNullable(expectedArrivalDate);
    }

    public InstantFilter expectedArrivalDate() {
        if (expectedArrivalDate == null) {
            setExpectedArrivalDate(new InstantFilter());
        }
        return expectedArrivalDate;
    }

    public void setExpectedArrivalDate(InstantFilter expectedArrivalDate) {
        this.expectedArrivalDate = expectedArrivalDate;
    }

    public LongFilter getSupplierId() {
        return supplierId;
    }

    public Optional<LongFilter> optionalSupplierId() {
        return Optional.ofNullable(supplierId);
    }

    public LongFilter supplierId() {
        if (supplierId == null) {
            setSupplierId(new LongFilter());
        }
        return supplierId;
    }

    public void setSupplierId(LongFilter supplierId) {
        this.supplierId = supplierId;
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
        final TrxOrderStockCriteria that = (TrxOrderStockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantityOrdered, that.quantityOrdered) &&
            Objects.equals(orderDate, that.orderDate) &&
            Objects.equals(expectedArrivalDate, that.expectedArrivalDate) &&
            Objects.equals(supplierId, that.supplierId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantityOrdered, orderDate, expectedArrivalDate, supplierId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderStockCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantityOrdered().map(f -> "quantityOrdered=" + f + ", ").orElse("") +
            optionalOrderDate().map(f -> "orderDate=" + f + ", ").orElse("") +
            optionalExpectedArrivalDate().map(f -> "expectedArrivalDate=" + f + ", ").orElse("") +
            optionalSupplierId().map(f -> "supplierId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
