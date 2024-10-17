package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.DeliveryStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxDelivery.
 */
@Table("trx_delivery")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxdelivery")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("delivery_address")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String deliveryAddress;

    @NotNull(message = "must not be null")
    @Column("delivery_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private DeliveryStatus deliveryStatus;

    @Column("assigned_driver")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String assignedDriver;

    @Column("estimated_delivery_time")
    private Instant estimatedDeliveryTime;

    @Transient
    private MstDriver driver;

    @Transient
    @JsonIgnoreProperties(value = { "deliveries", "mstCustomer" }, allowSetters = true)
    private TrxOrder trxOrder;

    @Column("driver_id")
    private Long driverId;

    @Column("trx_order_id")
    private Long trxOrderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxDelivery id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    public TrxDelivery deliveryAddress(String deliveryAddress) {
        this.setDeliveryAddress(deliveryAddress);
        return this;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DeliveryStatus getDeliveryStatus() {
        return this.deliveryStatus;
    }

    public TrxDelivery deliveryStatus(DeliveryStatus deliveryStatus) {
        this.setDeliveryStatus(deliveryStatus);
        return this;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getAssignedDriver() {
        return this.assignedDriver;
    }

    public TrxDelivery assignedDriver(String assignedDriver) {
        this.setAssignedDriver(assignedDriver);
        return this;
    }

    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public Instant getEstimatedDeliveryTime() {
        return this.estimatedDeliveryTime;
    }

    public TrxDelivery estimatedDeliveryTime(Instant estimatedDeliveryTime) {
        this.setEstimatedDeliveryTime(estimatedDeliveryTime);
        return this;
    }

    public void setEstimatedDeliveryTime(Instant estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public MstDriver getDriver() {
        return this.driver;
    }

    public void setDriver(MstDriver mstDriver) {
        this.driver = mstDriver;
        this.driverId = mstDriver != null ? mstDriver.getId() : null;
    }

    public TrxDelivery driver(MstDriver mstDriver) {
        this.setDriver(mstDriver);
        return this;
    }

    public TrxOrder getTrxOrder() {
        return this.trxOrder;
    }

    public void setTrxOrder(TrxOrder trxOrder) {
        this.trxOrder = trxOrder;
        this.trxOrderId = trxOrder != null ? trxOrder.getId() : null;
    }

    public TrxDelivery trxOrder(TrxOrder trxOrder) {
        this.setTrxOrder(trxOrder);
        return this;
    }

    public Long getDriverId() {
        return this.driverId;
    }

    public void setDriverId(Long mstDriver) {
        this.driverId = mstDriver;
    }

    public Long getTrxOrderId() {
        return this.trxOrderId;
    }

    public void setTrxOrderId(Long trxOrder) {
        this.trxOrderId = trxOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxDelivery)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxDelivery) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxDelivery{" +
            "id=" + getId() +
            ", deliveryAddress='" + getDeliveryAddress() + "'" +
            ", deliveryStatus='" + getDeliveryStatus() + "'" +
            ", assignedDriver='" + getAssignedDriver() + "'" +
            ", estimatedDeliveryTime='" + getEstimatedDeliveryTime() + "'" +
            "}";
    }
}
