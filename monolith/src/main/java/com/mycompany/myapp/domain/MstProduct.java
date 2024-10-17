package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MstProduct.
 */
@Table("mst_product")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstproduct")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column("description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull(message = "must not be null")
    @Column("price")
    private BigDecimal price;

    @NotNull(message = "must not be null")
    @Column("quantity")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer quantity;

    @Column("barcode")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String barcode;

    @Column("unit_size")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String unitSize;

    @Transient
    private MstCategory category;

    @Transient
    private MstBrand brand;

    @Transient
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private MstSupplier mstSupplier;

    @Column("category_id")
    private Long categoryId;

    @Column("brand_id")
    private Long brandId;

    @Column("mst_supplier_id")
    private Long mstSupplierId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstProduct id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MstProduct name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public MstProduct description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public MstProduct price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public MstProduct quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public MstProduct barcode(String barcode) {
        this.setBarcode(barcode);
        return this;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUnitSize() {
        return this.unitSize;
    }

    public MstProduct unitSize(String unitSize) {
        this.setUnitSize(unitSize);
        return this;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    public MstCategory getCategory() {
        return this.category;
    }

    public void setCategory(MstCategory mstCategory) {
        this.category = mstCategory;
        this.categoryId = mstCategory != null ? mstCategory.getId() : null;
    }

    public MstProduct category(MstCategory mstCategory) {
        this.setCategory(mstCategory);
        return this;
    }

    public MstBrand getBrand() {
        return this.brand;
    }

    public void setBrand(MstBrand mstBrand) {
        this.brand = mstBrand;
        this.brandId = mstBrand != null ? mstBrand.getId() : null;
    }

    public MstProduct brand(MstBrand mstBrand) {
        this.setBrand(mstBrand);
        return this;
    }

    public MstSupplier getMstSupplier() {
        return this.mstSupplier;
    }

    public void setMstSupplier(MstSupplier mstSupplier) {
        this.mstSupplier = mstSupplier;
        this.mstSupplierId = mstSupplier != null ? mstSupplier.getId() : null;
    }

    public MstProduct mstSupplier(MstSupplier mstSupplier) {
        this.setMstSupplier(mstSupplier);
        return this;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long mstCategory) {
        this.categoryId = mstCategory;
    }

    public Long getBrandId() {
        return this.brandId;
    }

    public void setBrandId(Long mstBrand) {
        this.brandId = mstBrand;
    }

    public Long getMstSupplierId() {
        return this.mstSupplierId;
    }

    public void setMstSupplierId(Long mstSupplier) {
        this.mstSupplierId = mstSupplier;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstProduct)) {
            return false;
        }
        return getId() != null && getId().equals(((MstProduct) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstProduct{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", quantity=" + getQuantity() +
            ", barcode='" + getBarcode() + "'" +
            ", unitSize='" + getUnitSize() + "'" +
            "}";
    }
}
