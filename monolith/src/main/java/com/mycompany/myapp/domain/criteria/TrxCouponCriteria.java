package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxCoupon} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxCouponResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-coupons?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxCouponCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private BigDecimalFilter discountAmount;

    private InstantFilter validUntil;

    private BigDecimalFilter minPurchase;

    private Boolean distinct;

    public TrxCouponCriteria() {}

    public TrxCouponCriteria(TrxCouponCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.discountAmount = other.optionalDiscountAmount().map(BigDecimalFilter::copy).orElse(null);
        this.validUntil = other.optionalValidUntil().map(InstantFilter::copy).orElse(null);
        this.minPurchase = other.optionalMinPurchase().map(BigDecimalFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxCouponCriteria copy() {
        return new TrxCouponCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public BigDecimalFilter getDiscountAmount() {
        return discountAmount;
    }

    public Optional<BigDecimalFilter> optionalDiscountAmount() {
        return Optional.ofNullable(discountAmount);
    }

    public BigDecimalFilter discountAmount() {
        if (discountAmount == null) {
            setDiscountAmount(new BigDecimalFilter());
        }
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimalFilter discountAmount) {
        this.discountAmount = discountAmount;
    }

    public InstantFilter getValidUntil() {
        return validUntil;
    }

    public Optional<InstantFilter> optionalValidUntil() {
        return Optional.ofNullable(validUntil);
    }

    public InstantFilter validUntil() {
        if (validUntil == null) {
            setValidUntil(new InstantFilter());
        }
        return validUntil;
    }

    public void setValidUntil(InstantFilter validUntil) {
        this.validUntil = validUntil;
    }

    public BigDecimalFilter getMinPurchase() {
        return minPurchase;
    }

    public Optional<BigDecimalFilter> optionalMinPurchase() {
        return Optional.ofNullable(minPurchase);
    }

    public BigDecimalFilter minPurchase() {
        if (minPurchase == null) {
            setMinPurchase(new BigDecimalFilter());
        }
        return minPurchase;
    }

    public void setMinPurchase(BigDecimalFilter minPurchase) {
        this.minPurchase = minPurchase;
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
        final TrxCouponCriteria that = (TrxCouponCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(discountAmount, that.discountAmount) &&
            Objects.equals(validUntil, that.validUntil) &&
            Objects.equals(minPurchase, that.minPurchase) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, discountAmount, validUntil, minPurchase, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxCouponCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalDiscountAmount().map(f -> "discountAmount=" + f + ", ").orElse("") +
            optionalValidUntil().map(f -> "validUntil=" + f + ", ").orElse("") +
            optionalMinPurchase().map(f -> "minPurchase=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
