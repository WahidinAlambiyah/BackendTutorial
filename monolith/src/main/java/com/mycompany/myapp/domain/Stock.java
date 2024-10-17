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
 * A Stock.
 */
@Table("stock")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "stock")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("quantity_available")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer quantityAvailable;

    @Column("reorder_level")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer reorderLevel;

    @Column("expiry_date")
    private Instant expiryDate;

    @Transient
    @JsonIgnoreProperties(value = { "category", "brand", "mstSupplier" }, allowSetters = true)
    private MstProduct product;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantityAvailable() {
        return this.quantityAvailable;
    }

    public Stock quantityAvailable(Integer quantityAvailable) {
        this.setQuantityAvailable(quantityAvailable);
        return this;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public Integer getReorderLevel() {
        return this.reorderLevel;
    }

    public Stock reorderLevel(Integer reorderLevel) {
        this.setReorderLevel(reorderLevel);
        return this;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Instant getExpiryDate() {
        return this.expiryDate;
    }

    public Stock expiryDate(Instant expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public MstProduct getProduct() {
        return this.product;
    }

    public void setProduct(MstProduct mstProduct) {
        this.product = mstProduct;
        this.productId = mstProduct != null ? mstProduct.getId() : null;
    }

    public Stock product(MstProduct mstProduct) {
        this.setProduct(mstProduct);
        return this;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long mstProduct) {
        this.productId = mstProduct;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        return getId() != null && getId().equals(((Stock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stock{" +
            "id=" + getId() +
            ", quantityAvailable=" + getQuantityAvailable() +
            ", reorderLevel=" + getReorderLevel() +
            ", expiryDate='" + getExpiryDate() + "'" +
            "}";
    }
}
