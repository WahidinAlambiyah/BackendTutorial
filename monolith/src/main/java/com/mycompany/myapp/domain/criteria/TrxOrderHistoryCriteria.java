package com.mycompany.myapp.domain.criteria;

import com.mycompany.myapp.domain.enumeration.OrderStatus;
import com.mycompany.myapp.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxOrderHistory} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxOrderHistoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-order-histories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderHistoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {}

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private OrderStatusFilter previousStatus;

    private OrderStatusFilter newStatus;

    private InstantFilter changeDate;

    private Boolean distinct;

    public TrxOrderHistoryCriteria() {}

    public TrxOrderHistoryCriteria(TrxOrderHistoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.previousStatus = other.optionalPreviousStatus().map(OrderStatusFilter::copy).orElse(null);
        this.newStatus = other.optionalNewStatus().map(OrderStatusFilter::copy).orElse(null);
        this.changeDate = other.optionalChangeDate().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxOrderHistoryCriteria copy() {
        return new TrxOrderHistoryCriteria(this);
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

    public OrderStatusFilter getPreviousStatus() {
        return previousStatus;
    }

    public Optional<OrderStatusFilter> optionalPreviousStatus() {
        return Optional.ofNullable(previousStatus);
    }

    public OrderStatusFilter previousStatus() {
        if (previousStatus == null) {
            setPreviousStatus(new OrderStatusFilter());
        }
        return previousStatus;
    }

    public void setPreviousStatus(OrderStatusFilter previousStatus) {
        this.previousStatus = previousStatus;
    }

    public OrderStatusFilter getNewStatus() {
        return newStatus;
    }

    public Optional<OrderStatusFilter> optionalNewStatus() {
        return Optional.ofNullable(newStatus);
    }

    public OrderStatusFilter newStatus() {
        if (newStatus == null) {
            setNewStatus(new OrderStatusFilter());
        }
        return newStatus;
    }

    public void setNewStatus(OrderStatusFilter newStatus) {
        this.newStatus = newStatus;
    }

    public InstantFilter getChangeDate() {
        return changeDate;
    }

    public Optional<InstantFilter> optionalChangeDate() {
        return Optional.ofNullable(changeDate);
    }

    public InstantFilter changeDate() {
        if (changeDate == null) {
            setChangeDate(new InstantFilter());
        }
        return changeDate;
    }

    public void setChangeDate(InstantFilter changeDate) {
        this.changeDate = changeDate;
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
        final TrxOrderHistoryCriteria that = (TrxOrderHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(previousStatus, that.previousStatus) &&
            Objects.equals(newStatus, that.newStatus) &&
            Objects.equals(changeDate, that.changeDate) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, previousStatus, newStatus, changeDate, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderHistoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPreviousStatus().map(f -> "previousStatus=" + f + ", ").orElse("") +
            optionalNewStatus().map(f -> "newStatus=" + f + ", ").orElse("") +
            optionalChangeDate().map(f -> "changeDate=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
