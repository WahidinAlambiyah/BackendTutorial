package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.ServiceType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Service.
 */
@Table("service")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "service")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Services implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column("description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Column("price")
    private BigDecimal price;

    @Column("duration_in_hours")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer durationInHours;

    @Column("service_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private ServiceType serviceType;

    @Transient
    @JsonIgnoreProperties(value = { "events", "services" }, allowSetters = true)
    private Testimonial testimonial;

    @Transient
    @JsonIgnoreProperties(value = { "tournaments", "service", "testimonial" }, allowSetters = true)
    private Set<Event> events = new HashSet<>();

    @Column("testimonial_id")
    private Long testimonialId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Services id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Services name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Services description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Services price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public Integer getDurationInHours() {
        return this.durationInHours;
    }

    public Services durationInHours(Integer durationInHours) {
        this.setDurationInHours(durationInHours);
        return this;
    }

    public void setDurationInHours(Integer durationInHours) {
        this.durationInHours = durationInHours;
    }

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    public Services serviceType(ServiceType serviceType) {
        this.setServiceType(serviceType);
        return this;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Testimonial getTestimonial() {
        return this.testimonial;
    }

    public void setTestimonial(Testimonial testimonial) {
        this.testimonial = testimonial;
        this.testimonialId = testimonial != null ? testimonial.getId() : null;
    }

    public Services testimonial(Testimonial testimonial) {
        this.setTestimonial(testimonial);
        return this;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setService(null));
        }
        if (events != null) {
            events.forEach(i -> i.setService(this));
        }
        this.events = events;
    }

    public Services events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public Services addEvent(Event event) {
        this.events.add(event);
        event.setService(this);
        return this;
    }

    public Services removeEvent(Event event) {
        this.events.remove(event);
        event.setService(null);
        return this;
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
        if (!(o instanceof Services)) {
            return false;
        }
        return getId() != null && getId().equals(((Services) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Service{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", durationInHours=" + getDurationInHours() +
            ", serviceType='" + getServiceType() + "'" +
            "}";
    }
}
