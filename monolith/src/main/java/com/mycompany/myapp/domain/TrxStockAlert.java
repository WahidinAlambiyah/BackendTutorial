package com.mycompany.myapp.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxStockAlert.
 */
@Table("trx_stock_alert")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxstockalert")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxStockAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("alert_threshold")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer alertThreshold;

    @NotNull(message = "must not be null")
    @Column("current_stock")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer currentStock;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxStockAlert id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAlertThreshold() {
        return this.alertThreshold;
    }

    public TrxStockAlert alertThreshold(Integer alertThreshold) {
        this.setAlertThreshold(alertThreshold);
        return this;
    }

    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public Integer getCurrentStock() {
        return this.currentStock;
    }

    public TrxStockAlert currentStock(Integer currentStock) {
        this.setCurrentStock(currentStock);
        return this;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxStockAlert)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxStockAlert) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxStockAlert{" +
            "id=" + getId() +
            ", alertThreshold=" + getAlertThreshold() +
            ", currentStock=" + getCurrentStock() +
            "}";
    }
}
