package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxOrderStock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderStockDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer quantityOrdered;

    @NotNull(message = "must not be null")
    private Instant orderDate;

    private Instant expectedArrivalDate;

    private MstSupplierDTO supplier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public Instant getExpectedArrivalDate() {
        return expectedArrivalDate;
    }

    public void setExpectedArrivalDate(Instant expectedArrivalDate) {
        this.expectedArrivalDate = expectedArrivalDate;
    }

    public MstSupplierDTO getSupplier() {
        return supplier;
    }

    public void setSupplier(MstSupplierDTO supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxOrderStockDTO)) {
            return false;
        }

        TrxOrderStockDTO trxOrderStockDTO = (TrxOrderStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxOrderStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderStockDTO{" +
            "id=" + getId() +
            ", quantityOrdered=" + getQuantityOrdered() +
            ", orderDate='" + getOrderDate() + "'" +
            ", expectedArrivalDate='" + getExpectedArrivalDate() + "'" +
            ", supplier=" + getSupplier() +
            "}";
    }
}
