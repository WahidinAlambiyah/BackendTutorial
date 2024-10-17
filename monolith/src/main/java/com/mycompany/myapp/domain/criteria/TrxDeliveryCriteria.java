package com.mycompany.myapp.domain.criteria;

import com.mycompany.myapp.domain.enumeration.DeliveryStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxDelivery} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxDeliveryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-deliveries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxDeliveryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DeliveryStatus
     */
    public static class DeliveryStatusFilter extends Filter<DeliveryStatus> {

        public DeliveryStatusFilter() {}

        public DeliveryStatusFilter(DeliveryStatusFilter filter) {
            super(filter);
        }

        @Override
        public DeliveryStatusFilter copy() {
            return new DeliveryStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter deliveryAddress;

    private DeliveryStatusFilter deliveryStatus;

    private StringFilter assignedDriver;

    private InstantFilter estimatedDeliveryTime;

    private LongFilter driverId;

    private LongFilter trxOrderId;

    private Boolean distinct;

    public TrxDeliveryCriteria() {}

    public TrxDeliveryCriteria(TrxDeliveryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.deliveryAddress = other.optionalDeliveryAddress().map(StringFilter::copy).orElse(null);
        this.deliveryStatus = other.optionalDeliveryStatus().map(DeliveryStatusFilter::copy).orElse(null);
        this.assignedDriver = other.optionalAssignedDriver().map(StringFilter::copy).orElse(null);
        this.estimatedDeliveryTime = other.optionalEstimatedDeliveryTime().map(InstantFilter::copy).orElse(null);
        this.driverId = other.optionalDriverId().map(LongFilter::copy).orElse(null);
        this.trxOrderId = other.optionalTrxOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxDeliveryCriteria copy() {
        return new TrxDeliveryCriteria(this);
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

    public StringFilter getDeliveryAddress() {
        return deliveryAddress;
    }

    public Optional<StringFilter> optionalDeliveryAddress() {
        return Optional.ofNullable(deliveryAddress);
    }

    public StringFilter deliveryAddress() {
        if (deliveryAddress == null) {
            setDeliveryAddress(new StringFilter());
        }
        return deliveryAddress;
    }

    public void setDeliveryAddress(StringFilter deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DeliveryStatusFilter getDeliveryStatus() {
        return deliveryStatus;
    }

    public Optional<DeliveryStatusFilter> optionalDeliveryStatus() {
        return Optional.ofNullable(deliveryStatus);
    }

    public DeliveryStatusFilter deliveryStatus() {
        if (deliveryStatus == null) {
            setDeliveryStatus(new DeliveryStatusFilter());
        }
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatusFilter deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public StringFilter getAssignedDriver() {
        return assignedDriver;
    }

    public Optional<StringFilter> optionalAssignedDriver() {
        return Optional.ofNullable(assignedDriver);
    }

    public StringFilter assignedDriver() {
        if (assignedDriver == null) {
            setAssignedDriver(new StringFilter());
        }
        return assignedDriver;
    }

    public void setAssignedDriver(StringFilter assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public InstantFilter getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public Optional<InstantFilter> optionalEstimatedDeliveryTime() {
        return Optional.ofNullable(estimatedDeliveryTime);
    }

    public InstantFilter estimatedDeliveryTime() {
        if (estimatedDeliveryTime == null) {
            setEstimatedDeliveryTime(new InstantFilter());
        }
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(InstantFilter estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public LongFilter getDriverId() {
        return driverId;
    }

    public Optional<LongFilter> optionalDriverId() {
        return Optional.ofNullable(driverId);
    }

    public LongFilter driverId() {
        if (driverId == null) {
            setDriverId(new LongFilter());
        }
        return driverId;
    }

    public void setDriverId(LongFilter driverId) {
        this.driverId = driverId;
    }

    public LongFilter getTrxOrderId() {
        return trxOrderId;
    }

    public Optional<LongFilter> optionalTrxOrderId() {
        return Optional.ofNullable(trxOrderId);
    }

    public LongFilter trxOrderId() {
        if (trxOrderId == null) {
            setTrxOrderId(new LongFilter());
        }
        return trxOrderId;
    }

    public void setTrxOrderId(LongFilter trxOrderId) {
        this.trxOrderId = trxOrderId;
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
        final TrxDeliveryCriteria that = (TrxDeliveryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(deliveryAddress, that.deliveryAddress) &&
            Objects.equals(deliveryStatus, that.deliveryStatus) &&
            Objects.equals(assignedDriver, that.assignedDriver) &&
            Objects.equals(estimatedDeliveryTime, that.estimatedDeliveryTime) &&
            Objects.equals(driverId, that.driverId) &&
            Objects.equals(trxOrderId, that.trxOrderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deliveryAddress, deliveryStatus, assignedDriver, estimatedDeliveryTime, driverId, trxOrderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxDeliveryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDeliveryAddress().map(f -> "deliveryAddress=" + f + ", ").orElse("") +
            optionalDeliveryStatus().map(f -> "deliveryStatus=" + f + ", ").orElse("") +
            optionalAssignedDriver().map(f -> "assignedDriver=" + f + ", ").orElse("") +
            optionalEstimatedDeliveryTime().map(f -> "estimatedDeliveryTime=" + f + ", ").orElse("") +
            optionalDriverId().map(f -> "driverId=" + f + ", ").orElse("") +
            optionalTrxOrderId().map(f -> "trxOrderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
