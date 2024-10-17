package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxProductHistory} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxProductHistoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-product-histories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxProductHistoryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter oldPrice;

    private BigDecimalFilter newPrice;

    private InstantFilter changeDate;

    private Boolean distinct;

    public TrxProductHistoryCriteria() {}

    public TrxProductHistoryCriteria(TrxProductHistoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.oldPrice = other.optionalOldPrice().map(BigDecimalFilter::copy).orElse(null);
        this.newPrice = other.optionalNewPrice().map(BigDecimalFilter::copy).orElse(null);
        this.changeDate = other.optionalChangeDate().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxProductHistoryCriteria copy() {
        return new TrxProductHistoryCriteria(this);
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

    public BigDecimalFilter getOldPrice() {
        return oldPrice;
    }

    public Optional<BigDecimalFilter> optionalOldPrice() {
        return Optional.ofNullable(oldPrice);
    }

    public BigDecimalFilter oldPrice() {
        if (oldPrice == null) {
            setOldPrice(new BigDecimalFilter());
        }
        return oldPrice;
    }

    public void setOldPrice(BigDecimalFilter oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimalFilter getNewPrice() {
        return newPrice;
    }

    public Optional<BigDecimalFilter> optionalNewPrice() {
        return Optional.ofNullable(newPrice);
    }

    public BigDecimalFilter newPrice() {
        if (newPrice == null) {
            setNewPrice(new BigDecimalFilter());
        }
        return newPrice;
    }

    public void setNewPrice(BigDecimalFilter newPrice) {
        this.newPrice = newPrice;
    }

    public InstantFilter getChangeDate() {
        return changeDate;
    }

    public Optional<InstantFilter> optionalChangeDate() {
        return Optional.ofNullable(changeDate);
    }

    public InstantFilter changeDate() {
        if (changeDate == null) {
            setChangeDate(new InstantFilter());
        }
        return changeDate;
    }

    public void setChangeDate(InstantFilter changeDate) {
        this.changeDate = changeDate;
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
        final TrxProductHistoryCriteria that = (TrxProductHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(oldPrice, that.oldPrice) &&
            Objects.equals(newPrice, that.newPrice) &&
            Objects.equals(changeDate, that.changeDate) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, oldPrice, newPrice, changeDate, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxProductHistoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOldPrice().map(f -> "oldPrice=" + f + ", ").orElse("") +
            optionalNewPrice().map(f -> "newPrice=" + f + ", ").orElse("") +
            optionalChangeDate().map(f -> "changeDate=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
