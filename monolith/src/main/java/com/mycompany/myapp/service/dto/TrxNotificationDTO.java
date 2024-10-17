package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxNotification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxNotificationDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String recipient;

    @NotNull(message = "must not be null")
    private String messageType;

    @NotNull(message = "must not be null")
    private String content;

    @NotNull(message = "must not be null")
    private Instant sentAt;

    private MstCustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
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
        if (!(o instanceof TrxNotificationDTO)) {
            return false;
        }

        TrxNotificationDTO trxNotificationDTO = (TrxNotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxNotificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxNotificationDTO{" +
            "id=" + getId() +
            ", recipient='" + getRecipient() + "'" +
            ", messageType='" + getMessageType() + "'" +
            ", content='" + getContent() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
