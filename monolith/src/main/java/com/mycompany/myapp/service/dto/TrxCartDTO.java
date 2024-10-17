package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxCart} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxCartDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private BigDecimal totalPrice;

    private MstCustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public MstCustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(MstCustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxCartDTO)) {
            return false;
        }

        TrxCartDTO trxCartDTO = (TrxCartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxCartDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxCartDTO{" +
            "id=" + getId() +
            ", totalPrice=" + getTotalPrice() +
            ", customer=" + getCustomer() +
            "}";
    }
}
