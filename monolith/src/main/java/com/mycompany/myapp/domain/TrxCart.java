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
 * A TrxCart.
 */
@Table("trx_cart")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxcart")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("total_price")
    private BigDecimal totalPrice;

    @Transient
    @JsonIgnoreProperties(value = { "orders" }, allowSetters = true)
    private MstCustomer customer;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxCart id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public TrxCart totalPrice(BigDecimal totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice != null ? totalPrice.stripTrailingZeros() : null;
    }

    public MstCustomer getCustomer() {
        return this.customer;
    }

    public void setCustomer(MstCustomer mstCustomer) {
        this.customer = mstCustomer;
        this.customerId = mstCustomer != null ? mstCustomer.getId() : null;
    }

    public TrxCart customer(MstCustomer mstCustomer) {
        this.setCustomer(mstCustomer);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long mstCustomer) {
        this.customerId = mstCustomer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxCart)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxCart) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxCart{" +
            "id=" + getId() +
            ", totalPrice=" + getTotalPrice() +
            "}";
    }
}
