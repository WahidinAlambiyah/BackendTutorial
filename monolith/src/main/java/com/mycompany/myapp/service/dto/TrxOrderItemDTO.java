package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxOrderItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderItemDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer quantity;

    @NotNull(message = "must not be null")
    private BigDecimal price;

    private TrxOrderDTO order;

    private MstProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TrxOrderDTO getOrder() {
        return order;
    }

    public void setOrder(TrxOrderDTO order) {
        this.order = order;
    }

    public MstProductDTO getProduct() {
        return product;
    }

    public void setProduct(MstProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxOrderItemDTO)) {
            return false;
        }

        TrxOrderItemDTO trxOrderItemDTO = (TrxOrderItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxOrderItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", price=" + getPrice() +
            ", order=" + getOrder() +
            ", product=" + getProduct() +
            "}";
    }
}
