package com.mycompany.myapp.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxProductHistory.
 */
@Table("trx_product_history")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxproducthistory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxProductHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("old_price")
    private BigDecimal oldPrice;

    @Column("new_price")
    private BigDecimal newPrice;

    @NotNull(message = "must not be null")
    @Column("change_date")
    private Instant changeDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxProductHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getOldPrice() {
        return this.oldPrice;
    }

    public TrxProductHistory oldPrice(BigDecimal oldPrice) {
        this.setOldPrice(oldPrice);
        return this;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice != null ? oldPrice.stripTrailingZeros() : null;
    }

    public BigDecimal getNewPrice() {
        return this.newPrice;
    }

    public TrxProductHistory newPrice(BigDecimal newPrice) {
        this.setNewPrice(newPrice);
        return this;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice != null ? newPrice.stripTrailingZeros() : null;
    }

    public Instant getChangeDate() {
        return this.changeDate;
    }

    public TrxProductHistory changeDate(Instant changeDate) {
        this.setChangeDate(changeDate);
        return this;
    }

    public void setChangeDate(Instant changeDate) {
        this.changeDate = changeDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxProductHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxProductHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxProductHistory{" +
            "id=" + getId() +
            ", oldPrice=" + getOldPrice() +
            ", newPrice=" + getNewPrice() +
            ", changeDate='" + getChangeDate() + "'" +
            "}";
    }
}
