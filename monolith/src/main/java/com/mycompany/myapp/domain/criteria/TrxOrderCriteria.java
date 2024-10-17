package com.mycompany.myapp.domain.criteria;

import com.mycompany.myapp.domain.enumeration.OrderStatus;
import com.mycompany.myapp.domain.enumeration.PaymentMethod;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxOrder} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderCriteria implements Serializable, Criteria {

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

    /**
     * Class for filtering PaymentMethod
     */
    public static class PaymentMethodFilter extends Filter<PaymentMethod> {

        public PaymentMethodFilter() {}

        public PaymentMethodFilter(PaymentMethodFilter filter) {
            super(filter);
        }

        @Override
        public PaymentMethodFilter copy() {
            return new PaymentMethodFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter orderDate;

    private InstantFilter deliveryDate;

    private OrderStatusFilter orderStatus;

    private PaymentMethodFilter paymentMethod;

    private BigDecimalFilter totalAmount;

    private LongFilter mstCustomerId;

    private Boolean distinct;

    public TrxOrderCriteria() {}

    public TrxOrderCriteria(TrxOrderCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.orderDate = other.optionalOrderDate().map(InstantFilter::copy).orElse(null);
        this.deliveryDate = other.optionalDeliveryDate().map(InstantFilter::copy).orElse(null);
        this.orderStatus = other.optionalOrderStatus().map(OrderStatusFilter::copy).orElse(null);
        this.paymentMethod = other.optionalPaymentMethod().map(PaymentMethodFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.mstCustomerId = other.optionalMstCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxOrderCriteria copy() {
        return new TrxOrderCriteria(this);
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

    public InstantFilter getDeliveryDate() {
        return deliveryDate;
    }

    public Optional<InstantFilter> optionalDeliveryDate() {
        return Optional.ofNullable(deliveryDate);
    }

    public InstantFilter deliveryDate() {
        if (deliveryDate == null) {
            setDeliveryDate(new InstantFilter());
        }
        return deliveryDate;
    }

    public void setDeliveryDate(InstantFilter deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatusFilter getOrderStatus() {
        return orderStatus;
    }

    public Optional<OrderStatusFilter> optionalOrderStatus() {
        return Optional.ofNullable(orderStatus);
    }

    public OrderStatusFilter orderStatus() {
        if (orderStatus == null) {
            setOrderStatus(new OrderStatusFilter());
        }
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusFilter orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentMethodFilter getPaymentMethod() {
        return paymentMethod;
    }

    public Optional<PaymentMethodFilter> optionalPaymentMethod() {
        return Optional.ofNullable(paymentMethod);
    }

    public PaymentMethodFilter paymentMethod() {
        if (paymentMethod == null) {
            setPaymentMethod(new PaymentMethodFilter());
        }
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodFilter paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LongFilter getMstCustomerId() {
        return mstCustomerId;
    }

    public Optional<LongFilter> optionalMstCustomerId() {
        return Optional.ofNullable(mstCustomerId);
    }

    public LongFilter mstCustomerId() {
        if (mstCustomerId == null) {
            setMstCustomerId(new LongFilter());
        }
        return mstCustomerId;
    }

    public void setMstCustomerId(LongFilter mstCustomerId) {
        this.mstCustomerId = mstCustomerId;
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
        final TrxOrderCriteria that = (TrxOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orderDate, that.orderDate) &&
            Objects.equals(deliveryDate, that.deliveryDate) &&
            Objects.equals(orderStatus, that.orderStatus) &&
            Objects.equals(paymentMethod, that.paymentMethod) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(mstCustomerId, that.mstCustomerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderDate, deliveryDate, orderStatus, paymentMethod, totalAmount, mstCustomerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrderDate().map(f -> "orderDate=" + f + ", ").orElse("") +
            optionalDeliveryDate().map(f -> "deliveryDate=" + f + ", ").orElse("") +
            optionalOrderStatus().map(f -> "orderStatus=" + f + ", ").orElse("") +
            optionalPaymentMethod().map(f -> "paymentMethod=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalMstCustomerId().map(f -> "mstCustomerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
