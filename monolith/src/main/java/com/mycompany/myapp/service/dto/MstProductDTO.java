package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstProduct} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstProductDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private String description;

    @NotNull(message = "must not be null")
    private BigDecimal price;

    @NotNull(message = "must not be null")
    private Integer quantity;

    private String barcode;

    private String unitSize;

    private MstCategoryDTO category;

    private MstBrandDTO brand;

    private MstSupplierDTO mstSupplier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    public MstCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(MstCategoryDTO category) {
        this.category = category;
    }

    public MstBrandDTO getBrand() {
        return brand;
    }

    public void setBrand(MstBrandDTO brand) {
        this.brand = brand;
    }

    public MstSupplierDTO getMstSupplier() {
        return mstSupplier;
    }

    public void setMstSupplier(MstSupplierDTO mstSupplier) {
        this.mstSupplier = mstSupplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstProductDTO)) {
            return false;
        }

        MstProductDTO mstProductDTO = (MstProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstProductDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstProductDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", quantity=" + getQuantity() +
            ", barcode='" + getBarcode() + "'" +
            ", unitSize='" + getUnitSize() + "'" +
            ", category=" + getCategory() +
            ", brand=" + getBrand() +
            ", mstSupplier=" + getMstSupplier() +
            "}";
    }
}
