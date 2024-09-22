package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.EventStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Event.
 */
@Table("event")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String title;

    @Column("description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull(message = "must not be null")
    @Column("date")
    private Instant date;

    @Column("location")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String location;

    @Column("capacity")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer capacity;

    @Column("price")
    private BigDecimal price;

    @Column("status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private EventStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "event" }, allowSetters = true)
    private Set<Tournament> tournaments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "testimonial", "events" }, allowSetters = true)
    private Services service;

    @Transient
    @JsonIgnoreProperties(value = { "events", "services" }, allowSetters = true)
    private Testimonial testimonial;

    @Column("service_id")
    private Long serviceId;

    @Column("testimonial_id")
    private Long testimonialId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Event id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Event title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Event description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDate() {
        return this.date;
    }

    public Event date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getLocation() {
        return this.location;
    }

    public Event location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public Event capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Event price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public EventStatus getStatus() {
        return this.status;
    }

    public Event status(EventStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Set<Tournament> getTournaments() {
        return this.tournaments;
    }

    public void setTournaments(Set<Tournament> tournaments) {
        if (this.tournaments != null) {
            this.tournaments.forEach(i -> i.setEvent(null));
        }
        if (tournaments != null) {
            tournaments.forEach(i -> i.setEvent(this));
        }
        this.tournaments = tournaments;
    }

    public Event tournaments(Set<Tournament> tournaments) {
        this.setTournaments(tournaments);
        return this;
    }

    public Event addTournament(Tournament tournament) {
        this.tournaments.add(tournament);
        tournament.setEvent(this);
        return this;
    }

    public Event removeTournament(Tournament tournament) {
        this.tournaments.remove(tournament);
        tournament.setEvent(null);
        return this;
    }

    public Services getService() {
        return this.service;
    }

    public void setService(Services service) {
        this.service = service;
        this.serviceId = service != null ? service.getId() : null;
    }

    public Event service(Services service) {
        this.setService(service);
        return this;
    }

    public Testimonial getTestimonial() {
        return this.testimonial;
    }

    public void setTestimonial(Testimonial testimonial) {
        this.testimonial = testimonial;
        this.testimonialId = testimonial != null ? testimonial.getId() : null;
    }

    public Event testimonial(Testimonial testimonial) {
        this.setTestimonial(testimonial);
        return this;
    }

    public Long getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(Long service) {
        this.serviceId = service;
    }

    public Long getTestimonialId() {
        return this.testimonialId;
    }

    public void setTestimonialId(Long testimonial) {
        this.testimonialId = testimonial;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return getId() != null && getId().equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", date='" + getDate() + "'" +
            ", location='" + getLocation() + "'" +
            ", capacity=" + getCapacity() +
            ", price=" + getPrice() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
