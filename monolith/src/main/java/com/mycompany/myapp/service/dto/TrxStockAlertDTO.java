package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxStockAlert} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxStockAlertDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer alertThreshold;

    @NotNull(message = "must not be null")
    private Integer currentStock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxStockAlertDTO)) {
            return false;
        }

        TrxStockAlertDTO trxStockAlertDTO = (TrxStockAlertDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxStockAlertDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxStockAlertDTO{" +
            "id=" + getId() +
            ", alertThreshold=" + getAlertThreshold() +
            ", currentStock=" + getCurrentStock() +
            "}";
    }
}
