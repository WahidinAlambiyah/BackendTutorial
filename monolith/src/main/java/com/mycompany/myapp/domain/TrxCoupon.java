package com.mycompany.myapp.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxCoupon.
 */
@Table("trx_coupon")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxcoupon")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @NotNull(message = "must not be null")
    @Column("discount_amount")
    private BigDecimal discountAmount;

    @NotNull(message = "must not be null")
    @Column("valid_until")
    private Instant validUntil;

    @Column("min_purchase")
    private BigDecimal minPurchase;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxCoupon id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public TrxCoupon code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public TrxCoupon discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount != null ? discountAmount.stripTrailingZeros() : null;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public TrxCoupon validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public BigDecimal getMinPurchase() {
        return this.minPurchase;
    }

    public TrxCoupon minPurchase(BigDecimal minPurchase) {
        this.setMinPurchase(minPurchase);
        return this;
    }

    public void setMinPurchase(BigDecimal minPurchase) {
        this.minPurchase = minPurchase != null ? minPurchase.stripTrailingZeros() : null;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxCoupon)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxCoupon) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxCoupon{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", discountAmount=" + getDiscountAmount() +
            ", validUntil='" + getValidUntil() + "'" +
            ", minPurchase=" + getMinPurchase() +
            "}";
    }
}
