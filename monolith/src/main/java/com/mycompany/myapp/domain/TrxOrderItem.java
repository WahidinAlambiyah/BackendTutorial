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
 * A TrxOrderItem.
 */
@Table("trx_order_item")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxorderitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("quantity")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer quantity;

    @NotNull(message = "must not be null")
    @Column("price")
    private BigDecimal price;

    @Transient
    @JsonIgnoreProperties(value = { "deliveries", "mstCustomer" }, allowSetters = true)
    private TrxOrder order;

    @Transient
    @JsonIgnoreProperties(value = { "category", "brand", "mstSupplier" }, allowSetters = true)
    private MstProduct product;

    @Column("order_id")
    private Long orderId;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxOrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public TrxOrderItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public TrxOrderItem price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public TrxOrder getOrder() {
        return this.order;
    }

    public void setOrder(TrxOrder trxOrder) {
        this.order = trxOrder;
        this.orderId = trxOrder != null ? trxOrder.getId() : null;
    }

    public TrxOrderItem order(TrxOrder trxOrder) {
        this.setOrder(trxOrder);
        return this;
    }

    public MstProduct getProduct() {
        return this.product;
    }

    public void setProduct(MstProduct mstProduct) {
        this.product = mstProduct;
        this.productId = mstProduct != null ? mstProduct.getId() : null;
    }

    public TrxOrderItem product(MstProduct mstProduct) {
        this.setProduct(mstProduct);
        return this;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long trxOrder) {
        this.orderId = trxOrder;
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
        if (!(o instanceof TrxOrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxOrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", price=" + getPrice() +
            "}";
    }
}
