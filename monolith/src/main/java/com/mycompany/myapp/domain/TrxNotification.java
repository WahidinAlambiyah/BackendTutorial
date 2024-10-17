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
 * A TrxNotification.
 */
@Table("trx_notification")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxnotification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("recipient")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String recipient;

    @NotNull(message = "must not be null")
    @Column("message_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String messageType;

    @NotNull(message = "must not be null")
    @Column("content")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String content;

    @NotNull(message = "must not be null")
    @Column("sent_at")
    private Instant sentAt;

    @Transient
    @JsonIgnoreProperties(value = { "orders" }, allowSetters = true)
    private MstCustomer customer;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxNotification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public TrxNotification recipient(String recipient) {
        this.setRecipient(recipient);
        return this;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public TrxNotification messageType(String messageType) {
        this.setMessageType(messageType);
        return this;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return this.content;
    }

    public TrxNotification content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public TrxNotification sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public MstCustomer getCustomer() {
        return this.customer;
    }

    public void setCustomer(MstCustomer mstCustomer) {
        this.customer = mstCustomer;
        this.customerId = mstCustomer != null ? mstCustomer.getId() : null;
    }

    public TrxNotification customer(MstCustomer mstCustomer) {
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
        if (!(o instanceof TrxNotification)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxNotification) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxNotification{" +
            "id=" + getId() +
            ", recipient='" + getRecipient() + "'" +
            ", messageType='" + getMessageType() + "'" +
            ", content='" + getContent() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            "}";
    }
}
