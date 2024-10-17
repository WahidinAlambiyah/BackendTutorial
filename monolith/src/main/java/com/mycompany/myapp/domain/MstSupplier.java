package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MstSupplier.
 */
@Table("mst_supplier")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstsupplier")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstSupplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column("contact_info")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String contactInfo;

    @Column("address")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String address;

    @Min(value = 1)
    @Max(value = 5)
    @Column("rating")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer rating;

    @Transient
    @JsonIgnoreProperties(value = { "category", "brand", "mstSupplier" }, allowSetters = true)
    private Set<MstProduct> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstSupplier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MstSupplier name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return this.contactInfo;
    }

    public MstSupplier contactInfo(String contactInfo) {
        this.setContactInfo(contactInfo);
        return this;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAddress() {
        return this.address;
    }

    public MstSupplier address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRating() {
        return this.rating;
    }

    public MstSupplier rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Set<MstProduct> getProducts() {
        return this.products;
    }

    public void setProducts(Set<MstProduct> mstProducts) {
        if (this.products != null) {
            this.products.forEach(i -> i.setMstSupplier(null));
        }
        if (mstProducts != null) {
            mstProducts.forEach(i -> i.setMstSupplier(this));
        }
        this.products = mstProducts;
    }

    public MstSupplier products(Set<MstProduct> mstProducts) {
        this.setProducts(mstProducts);
        return this;
    }

    public MstSupplier addProduct(MstProduct mstProduct) {
        this.products.add(mstProduct);
        mstProduct.setMstSupplier(this);
        return this;
    }

    public MstSupplier removeProduct(MstProduct mstProduct) {
        this.products.remove(mstProduct);
        mstProduct.setMstSupplier(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstSupplier)) {
            return false;
        }
        return getId() != null && getId().equals(((MstSupplier) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstSupplier{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", contactInfo='" + getContactInfo() + "'" +
            ", address='" + getAddress() + "'" +
            ", rating=" + getRating() +
            "}";
    }
}
