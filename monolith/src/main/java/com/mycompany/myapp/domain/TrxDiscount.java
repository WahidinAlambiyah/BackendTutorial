package com.mycompany.myapp.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxDiscount.
 */
@Table("trx_discount")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxdiscount")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxDiscount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("discount_percentage")
    private Float discountPercentage;

    @NotNull(message = "must not be null")
    @Column("start_date")
    private Instant startDate;

    @NotNull(message = "must not be null")
    @Column("end_date")
    private Instant endDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxDiscount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getDiscountPercentage() {
        return this.discountPercentage;
    }

    public TrxDiscount discountPercentage(Float discountPercentage) {
        this.setDiscountPercentage(discountPercentage);
        return this;
    }

    public void setDiscountPercentage(Float discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public TrxDiscount startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public TrxDiscount endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxDiscount)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxDiscount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxDiscount{" +
            "id=" + getId() +
            ", discountPercentage=" + getDiscountPercentage() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
