package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxCoupon} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxCouponDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String code;

    @NotNull(message = "must not be null")
    private BigDecimal discountAmount;

    @NotNull(message = "must not be null")
    private Instant validUntil;

    private BigDecimal minPurchase;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public BigDecimal getMinPurchase() {
        return minPurchase;
    }

    public void setMinPurchase(BigDecimal minPurchase) {
        this.minPurchase = minPurchase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxCouponDTO)) {
            return false;
        }

        TrxCouponDTO trxCouponDTO = (TrxCouponDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxCouponDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxCouponDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", discountAmount=" + getDiscountAmount() +
            ", validUntil='" + getValidUntil() + "'" +
            ", minPurchase=" + getMinPurchase() +
            "}";
    }
}
