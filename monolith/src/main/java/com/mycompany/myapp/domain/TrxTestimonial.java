package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxTestimonial.
 */
@Table("trx_testimonial")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxtestimonial")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxTestimonial implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column("feedback")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String feedback;

    @NotNull(message = "must not be null")
    @Min(value = 1)
    @Max(value = 5)
    @Column("rating")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer rating;

    @NotNull(message = "must not be null")
    @Column("date")
    private Instant date;

    @Transient
    @JsonIgnoreProperties(value = { "tournaments", "service", "testimonial" }, allowSetters = true)
    private Set<TrxEvent> events = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "testimonial", "events" }, allowSetters = true)
    private Set<MstService> services = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxTestimonial id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TrxTestimonial name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeedback() {
        return this.feedback;
    }

    public TrxTestimonial feedback(String feedback) {
        this.setFeedback(feedback);
        return this;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getRating() {
        return this.rating;
    }

    public TrxTestimonial rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Instant getDate() {
        return this.date;
    }

    public TrxTestimonial date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Set<TrxEvent> getEvents() {
        return this.events;
    }

    public void setEvents(Set<TrxEvent> trxEvents) {
        if (this.events != null) {
            this.events.forEach(i -> i.setTestimonial(null));
        }
        if (trxEvents != null) {
            trxEvents.forEach(i -> i.setTestimonial(this));
        }
        this.events = trxEvents;
    }

    public TrxTestimonial events(Set<TrxEvent> trxEvents) {
        this.setEvents(trxEvents);
        return this;
    }

    public TrxTestimonial addEvent(TrxEvent trxEvent) {
        this.events.add(trxEvent);
        trxEvent.setTestimonial(this);
        return this;
    }

    public TrxTestimonial removeEvent(TrxEvent trxEvent) {
        this.events.remove(trxEvent);
        trxEvent.setTestimonial(null);
        return this;
    }

    public Set<MstService> getServices() {
        return this.services;
    }

    public void setServices(Set<MstService> mstServices) {
        if (this.services != null) {
            this.services.forEach(i -> i.setTestimonial(null));
        }
        if (mstServices != null) {
            mstServices.forEach(i -> i.setTestimonial(this));
        }
        this.services = mstServices;
    }

    public TrxTestimonial services(Set<MstService> mstServices) {
        this.setServices(mstServices);
        return this;
    }

    public TrxTestimonial addService(MstService mstService) {
        this.services.add(mstService);
        mstService.setTestimonial(this);
        return this;
    }

    public TrxTestimonial removeService(MstService mstService) {
        this.services.remove(mstService);
        mstService.setTestimonial(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxTestimonial)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxTestimonial) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxTestimonial{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", feedback='" + getFeedback() + "'" +
            ", rating=" + getRating() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
