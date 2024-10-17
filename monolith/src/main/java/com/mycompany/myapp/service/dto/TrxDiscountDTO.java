package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxDiscount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxDiscountDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Float discountPercentage;

    @NotNull(message = "must not be null")
    private Instant startDate;

    @NotNull(message = "must not be null")
    private Instant endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Float discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxDiscountDTO)) {
            return false;
        }

        TrxDiscountDTO trxDiscountDTO = (TrxDiscountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxDiscountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxDiscountDTO{" +
            "id=" + getId() +
            ", discountPercentage=" + getDiscountPercentage() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
