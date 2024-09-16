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
 * A Testimonial.
 */
@Table("testimonial")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "testimonial")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Testimonial implements Serializable {

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
    private Set<Event> events = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "testimonial", "events" }, allowSetters = true)
    private Set<Services> services = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Testimonial id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Testimonial name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeedback() {
        return this.feedback;
    }

    public Testimonial feedback(String feedback) {
        this.setFeedback(feedback);
        return this;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Testimonial rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Instant getDate() {
        return this.date;
    }

    public Testimonial date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setTestimonial(null));
        }
        if (events != null) {
            events.forEach(i -> i.setTestimonial(this));
        }
        this.events = events;
    }

    public Testimonial events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public Testimonial addEvent(Event event) {
        this.events.add(event);
        event.setTestimonial(this);
        return this;
    }

    public Testimonial removeEvent(Event event) {
        this.events.remove(event);
        event.setTestimonial(null);
        return this;
    }

    public Set<Services> getServices() {
        return this.services;
    }

    public void setServices(Set<Services> services) {
        if (this.services != null) {
            this.services.forEach(i -> i.setTestimonial(null));
        }
        if (services != null) {
            services.forEach(i -> i.setTestimonial(this));
        }
        this.services = services;
    }

    public Testimonial services(Set<Services> services) {
        this.setServices(services);
        return this;
    }

    public Testimonial addService(Services service) {
        this.services.add(service);
        service.setTestimonial(this);
        return this;
    }

    public Testimonial removeService(Services service) {
        this.services.remove(service);
        service.setTestimonial(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Testimonial)) {
            return false;
        }
        return getId() != null && getId().equals(((Testimonial) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Testimonial{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", feedback='" + getFeedback() + "'" +
            ", rating=" + getRating() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
