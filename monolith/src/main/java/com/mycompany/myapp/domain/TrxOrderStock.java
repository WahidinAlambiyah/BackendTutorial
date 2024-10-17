package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxOrderStock.
 */
@Table("trx_order_stock")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxorderstock")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("quantity_ordered")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer quantityOrdered;

    @NotNull(message = "must not be null")
    @Column("order_date")
    private Instant orderDate;

    @Column("expected_arrival_date")
    private Instant expectedArrivalDate;

    @Transient
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private MstSupplier supplier;

    @Column("supplier_id")
    private Long supplierId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxOrderStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantityOrdered() {
        return this.quantityOrdered;
    }

    public TrxOrderStock quantityOrdered(Integer quantityOrdered) {
        this.setQuantityOrdered(quantityOrdered);
        return this;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public Instant getOrderDate() {
        return this.orderDate;
    }

    public TrxOrderStock orderDate(Instant orderDate) {
        this.setOrderDate(orderDate);
        return this;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public Instant getExpectedArrivalDate() {
        return this.expectedArrivalDate;
    }

    public TrxOrderStock expectedArrivalDate(Instant expectedArrivalDate) {
        this.setExpectedArrivalDate(expectedArrivalDate);
        return this;
    }

    public void setExpectedArrivalDate(Instant expectedArrivalDate) {
        this.expectedArrivalDate = expectedArrivalDate;
    }

    public MstSupplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(MstSupplier mstSupplier) {
        this.supplier = mstSupplier;
        this.supplierId = mstSupplier != null ? mstSupplier.getId() : null;
    }

    public TrxOrderStock supplier(MstSupplier mstSupplier) {
        this.setSupplier(mstSupplier);
        return this;
    }

    public Long getSupplierId() {
        return this.supplierId;
    }

    public void setSupplierId(Long mstSupplier) {
        this.supplierId = mstSupplier;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxOrderStock)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxOrderStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderStock{" +
            "id=" + getId() +
            ", quantityOrdered=" + getQuantityOrdered() +
            ", orderDate='" + getOrderDate() + "'" +
            ", expectedArrivalDate='" + getExpectedArrivalDate() + "'" +
            "}";
    }
}
