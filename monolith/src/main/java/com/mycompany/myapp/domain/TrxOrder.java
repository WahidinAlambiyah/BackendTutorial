package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.OrderStatus;
import com.mycompany.myapp.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxOrder.
 */
@Table("trx_order")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxorder")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("order_date")
    private Instant orderDate;

    @Column("delivery_date")
    private Instant deliveryDate;

    @NotNull(message = "must not be null")
    @Column("order_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private OrderStatus orderStatus;

    @NotNull(message = "must not be null")
    @Column("payment_method")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private PaymentMethod paymentMethod;

    @NotNull(message = "must not be null")
    @Column("total_amount")
    private BigDecimal totalAmount;

    @Transient
    @JsonIgnoreProperties(value = { "driver", "trxOrder" }, allowSetters = true)
    private Set<TrxDelivery> deliveries = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "orders" }, allowSetters = true)
    private MstCustomer mstCustomer;

    @Column("mst_customer_id")
    private Long mstCustomerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOrderDate() {
        return this.orderDate;
    }

    public TrxOrder orderDate(Instant orderDate) {
        this.setOrderDate(orderDate);
        return this;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public Instant getDeliveryDate() {
        return this.deliveryDate;
    }

    public TrxOrder deliveryDate(Instant deliveryDate) {
        this.setDeliveryDate(deliveryDate);
        return this;
    }

    public void setDeliveryDate(Instant deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getOrderStatus() {
        return this.orderStatus;
    }

    public TrxOrder orderStatus(OrderStatus orderStatus) {
        this.setOrderStatus(orderStatus);
        return this;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public TrxOrder paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public TrxOrder totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount.stripTrailingZeros() : null;
    }

    public Set<TrxDelivery> getDeliveries() {
        return this.deliveries;
    }

    public void setDeliveries(Set<TrxDelivery> trxDeliveries) {
        if (this.deliveries != null) {
            this.deliveries.forEach(i -> i.setTrxOrder(null));
        }
        if (trxDeliveries != null) {
            trxDeliveries.forEach(i -> i.setTrxOrder(this));
        }
        this.deliveries = trxDeliveries;
    }

    public TrxOrder deliveries(Set<TrxDelivery> trxDeliveries) {
        this.setDeliveries(trxDeliveries);
        return this;
    }

    public TrxOrder addDelivery(TrxDelivery trxDelivery) {
        this.deliveries.add(trxDelivery);
        trxDelivery.setTrxOrder(this);
        return this;
    }

    public TrxOrder removeDelivery(TrxDelivery trxDelivery) {
        this.deliveries.remove(trxDelivery);
        trxDelivery.setTrxOrder(null);
        return this;
    }

    public MstCustomer getMstCustomer() {
        return this.mstCustomer;
    }

    public void setMstCustomer(MstCustomer mstCustomer) {
        this.mstCustomer = mstCustomer;
        this.mstCustomerId = mstCustomer != null ? mstCustomer.getId() : null;
    }

    public TrxOrder mstCustomer(MstCustomer mstCustomer) {
        this.setMstCustomer(mstCustomer);
        return this;
    }

    public Long getMstCustomerId() {
        return this.mstCustomerId;
    }

    public void setMstCustomerId(Long mstCustomer) {
        this.mstCustomerId = mstCustomer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrder{" +
            "id=" + getId() +
            ", orderDate='" + getOrderDate() + "'" +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            ", orderStatus='" + getOrderStatus() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", totalAmount=" + getTotalAmount() +
            "}";
    }
}
