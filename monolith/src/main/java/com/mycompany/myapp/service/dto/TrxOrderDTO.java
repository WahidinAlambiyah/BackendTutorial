package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.OrderStatus;
import com.mycompany.myapp.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant orderDate;

    private Instant deliveryDate;

    @NotNull(message = "must not be null")
    private OrderStatus orderStatus;

    @NotNull(message = "must not be null")
    private PaymentMethod paymentMethod;

    @NotNull(message = "must not be null")
    private BigDecimal totalAmount;

    private MstCustomerDTO mstCustomer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public Instant getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Instant deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public MstCustomerDTO getMstCustomer() {
        return mstCustomer;
    }

    public void setMstCustomer(MstCustomerDTO mstCustomer) {
        this.mstCustomer = mstCustomer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxOrderDTO)) {
            return false;
        }

        TrxOrderDTO trxOrderDTO = (TrxOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderDTO{" +
            "id=" + getId() +
            ", orderDate='" + getOrderDate() + "'" +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            ", orderStatus='" + getOrderStatus() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", mstCustomer=" + getMstCustomer() +
            "}";
    }
}
