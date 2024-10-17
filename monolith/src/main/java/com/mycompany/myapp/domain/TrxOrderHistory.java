package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.OrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxOrderHistory.
 */
@Table("trx_order_history")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxorderhistory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxOrderHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("previous_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private OrderStatus previousStatus;

    @NotNull(message = "must not be null")
    @Column("new_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private OrderStatus newStatus;

    @NotNull(message = "must not be null")
    @Column("change_date")
    private Instant changeDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxOrderHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getPreviousStatus() {
        return this.previousStatus;
    }

    public TrxOrderHistory previousStatus(OrderStatus previousStatus) {
        this.setPreviousStatus(previousStatus);
        return this;
    }

    public void setPreviousStatus(OrderStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public OrderStatus getNewStatus() {
        return this.newStatus;
    }

    public TrxOrderHistory newStatus(OrderStatus newStatus) {
        this.setNewStatus(newStatus);
        return this;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Instant getChangeDate() {
        return this.changeDate;
    }

    public TrxOrderHistory changeDate(Instant changeDate) {
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
        if (!(o instanceof TrxOrderHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxOrderHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxOrderHistory{" +
            "id=" + getId() +
            ", previousStatus='" + getPreviousStatus() + "'" +
            ", newStatus='" + getNewStatus() + "'" +
            ", changeDate='" + getChangeDate() + "'" +
            "}";
    }
}
