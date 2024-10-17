package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.DeliveryStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxDelivery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxDeliveryDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String deliveryAddress;

    @NotNull(message = "must not be null")
    private DeliveryStatus deliveryStatus;

    private String assignedDriver;

    private Instant estimatedDeliveryTime;

    private MstDriverDTO driver;

    private TrxOrderDTO trxOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public Instant getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(Instant estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public MstDriverDTO getDriver() {
        return driver;
    }

    public void setDriver(MstDriverDTO driver) {
        this.driver = driver;
    }

    public TrxOrderDTO getTrxOrder() {
        return trxOrder;
    }

    public void setTrxOrder(TrxOrderDTO trxOrder) {
        this.trxOrder = trxOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxDeliveryDTO)) {
            return false;
        }

        TrxDeliveryDTO trxDeliveryDTO = (TrxDeliveryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxDeliveryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxDeliveryDTO{" +
            "id=" + getId() +
            ", deliveryAddress='" + getDeliveryAddress() + "'" +
            ", deliveryStatus='" + getDeliveryStatus() + "'" +
            ", assignedDriver='" + getAssignedDriver() + "'" +
            ", estimatedDeliveryTime='" + getEstimatedDeliveryTime() + "'" +
            ", driver=" + getDriver() +
            ", trxOrder=" + getTrxOrder() +
            "}";
    }
}
