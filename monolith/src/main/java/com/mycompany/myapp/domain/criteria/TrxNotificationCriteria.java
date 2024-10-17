package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxNotification} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxNotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxNotificationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter recipient;

    private StringFilter messageType;

    private StringFilter content;

    private InstantFilter sentAt;

    private LongFilter customerId;

    private Boolean distinct;

    public TrxNotificationCriteria() {}

    public TrxNotificationCriteria(TrxNotificationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.recipient = other.optionalRecipient().map(StringFilter::copy).orElse(null);
        this.messageType = other.optionalMessageType().map(StringFilter::copy).orElse(null);
        this.content = other.optionalContent().map(StringFilter::copy).orElse(null);
        this.sentAt = other.optionalSentAt().map(InstantFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxNotificationCriteria copy() {
        return new TrxNotificationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getRecipient() {
        return recipient;
    }

    public Optional<StringFilter> optionalRecipient() {
        return Optional.ofNullable(recipient);
    }

    public StringFilter recipient() {
        if (recipient == null) {
            setRecipient(new StringFilter());
        }
        return recipient;
    }

    public void setRecipient(StringFilter recipient) {
        this.recipient = recipient;
    }

    public StringFilter getMessageType() {
        return messageType;
    }

    public Optional<StringFilter> optionalMessageType() {
        return Optional.ofNullable(messageType);
    }

    public StringFilter messageType() {
        if (messageType == null) {
            setMessageType(new StringFilter());
        }
        return messageType;
    }

    public void setMessageType(StringFilter messageType) {
        this.messageType = messageType;
    }

    public StringFilter getContent() {
        return content;
    }

    public Optional<StringFilter> optionalContent() {
        return Optional.ofNullable(content);
    }

    public StringFilter content() {
        if (content == null) {
            setContent(new StringFilter());
        }
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public InstantFilter getSentAt() {
        return sentAt;
    }

    public Optional<InstantFilter> optionalSentAt() {
        return Optional.ofNullable(sentAt);
    }

    public InstantFilter sentAt() {
        if (sentAt == null) {
            setSentAt(new InstantFilter());
        }
        return sentAt;
    }

    public void setSentAt(InstantFilter sentAt) {
        this.sentAt = sentAt;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TrxNotificationCriteria that = (TrxNotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(recipient, that.recipient) &&
            Objects.equals(messageType, that.messageType) &&
            Objects.equals(content, that.content) &&
            Objects.equals(sentAt, that.sentAt) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recipient, messageType, content, sentAt, customerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxNotificationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRecipient().map(f -> "recipient=" + f + ", ").orElse("") +
            optionalMessageType().map(f -> "messageType=" + f + ", ").orElse("") +
            optionalContent().map(f -> "content=" + f + ", ").orElse("") +
            optionalSentAt().map(f -> "sentAt=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
