package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxProductHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxProductHistoryDTO implements Serializable {

    private Long id;

    private BigDecimal oldPrice;

    private BigDecimal newPrice;

    @NotNull(message = "must not be null")
    private Instant changeDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
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
        if (!(o instanceof TrxProductHistoryDTO)) {
            return false;
        }

        TrxProductHistoryDTO trxProductHistoryDTO = (TrxProductHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxProductHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxProductHistoryDTO{" +
            "id=" + getId() +
            ", oldPrice=" + getOldPrice() +
            ", newPrice=" + getNewPrice() +
            ", changeDate='" + getChangeDate() + "'" +
            "}";
    }
}
