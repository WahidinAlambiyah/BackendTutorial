package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.OrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxOrderHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderHistoryDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private OrderStatus previousStatus;

    @NotNull(message = "must not be null")
    private OrderStatus newStatus;

    @NotNull(message = "must not be null")
    private Instant changeDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(OrderStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Instant getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Instant changeDate) {
        this.changeDate = changeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxOrderHistoryDTO)) {
            return false;
        }

        TrxOrderHistoryDTO trxOrderHistoryDTO = (TrxOrderHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxOrderHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderHistoryDTO{" +
            "id=" + getId() +
            ", previousStatus='" + getPreviousStatus() + "'" +
            ", newStatus='" + getNewStatus() + "'" +
            ", changeDate='" + getChangeDate() + "'" +
            "}";
    }
}
