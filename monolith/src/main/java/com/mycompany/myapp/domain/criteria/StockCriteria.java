package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Stock} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.StockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter quantityAvailable;

    private IntegerFilter reorderLevel;

    private InstantFilter expiryDate;

    private LongFilter productId;

    private Boolean distinct;

    public StockCriteria() {}

    public StockCriteria(StockCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantityAvailable = other.optionalQuantityAvailable().map(IntegerFilter::copy).orElse(null);
        this.reorderLevel = other.optionalReorderLevel().map(IntegerFilter::copy).orElse(null);
        this.expiryDate = other.optionalExpiryDate().map(InstantFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StockCriteria copy() {
        return new StockCriteria(this);
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

    public IntegerFilter getQuantityAvailable() {
        return quantityAvailable;
    }

    public Optional<IntegerFilter> optionalQuantityAvailable() {
        return Optional.ofNullable(quantityAvailable);
    }

    public IntegerFilter quantityAvailable() {
        if (quantityAvailable == null) {
            setQuantityAvailable(new IntegerFilter());
        }
        return quantityAvailable;
    }

    public void setQuantityAvailable(IntegerFilter quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public IntegerFilter getReorderLevel() {
        return reorderLevel;
    }

    public Optional<IntegerFilter> optionalReorderLevel() {
        return Optional.ofNullable(reorderLevel);
    }

    public IntegerFilter reorderLevel() {
        if (reorderLevel == null) {
            setReorderLevel(new IntegerFilter());
        }
        return reorderLevel;
    }

    public void setReorderLevel(IntegerFilter reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public InstantFilter getExpiryDate() {
        return expiryDate;
    }

    public Optional<InstantFilter> optionalExpiryDate() {
        return Optional.ofNullable(expiryDate);
    }

    public InstantFilter expiryDate() {
        if (expiryDate == null) {
            setExpiryDate(new InstantFilter());
        }
        return expiryDate;
    }

    public void setExpiryDate(InstantFilter expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public Optional<LongFilter> optionalProductId() {
        return Optional.ofNullable(productId);
    }

    public LongFilter productId() {
        if (productId == null) {
            setProductId(new LongFilter());
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
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
        final StockCriteria that = (StockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantityAvailable, that.quantityAvailable) &&
            Objects.equals(reorderLevel, that.reorderLevel) &&
            Objects.equals(expiryDate, that.expiryDate) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantityAvailable, reorderLevel, expiryDate, productId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantityAvailable().map(f -> "quantityAvailable=" + f + ", ").orElse("") +
            optionalReorderLevel().map(f -> "reorderLevel=" + f + ", ").orElse("") +
            optionalExpiryDate().map(f -> "expiryDate=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
