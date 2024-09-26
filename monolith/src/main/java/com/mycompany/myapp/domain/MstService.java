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
 * A MstService.
 */
@Table("mst_service")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstservice")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstService implements Serializable {

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
    private TrxTestimonial testimonial;

    @Transient
    @JsonIgnoreProperties(value = { "tournaments", "service", "testimonial" }, allowSetters = true)
    private Set<TrxEvent> events = new HashSet<>();

    @Column("testimonial_id")
    private Long testimonialId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstService id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MstService name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public MstService description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public MstService price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public Integer getDurationInHours() {
        return this.durationInHours;
    }

    public MstService durationInHours(Integer durationInHours) {
        this.setDurationInHours(durationInHours);
        return this;
    }

    public void setDurationInHours(Integer durationInHours) {
        this.durationInHours = durationInHours;
    }

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    public MstService serviceType(ServiceType serviceType) {
        this.setServiceType(serviceType);
        return this;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public TrxTestimonial getTestimonial() {
        return this.testimonial;
    }

    public void setTestimonial(TrxTestimonial trxTestimonial) {
        this.testimonial = trxTestimonial;
        this.testimonialId = trxTestimonial != null ? trxTestimonial.getId() : null;
    }

    public MstService testimonial(TrxTestimonial trxTestimonial) {
        this.setTestimonial(trxTestimonial);
        return this;
    }

    public Set<TrxEvent> getEvents() {
        return this.events;
    }

    public void setEvents(Set<TrxEvent> trxEvents) {
        if (this.events != null) {
            this.events.forEach(i -> i.setService(null));
        }
        if (trxEvents != null) {
            trxEvents.forEach(i -> i.setService(this));
        }
        this.events = trxEvents;
    }

    public MstService events(Set<TrxEvent> trxEvents) {
        this.setEvents(trxEvents);
        return this;
    }

    public MstService addEvent(TrxEvent trxEvent) {
        this.events.add(trxEvent);
        trxEvent.setService(this);
        return this;
    }

    public MstService removeEvent(TrxEvent trxEvent) {
        this.events.remove(trxEvent);
        trxEvent.setService(null);
        return this;
    }

    public Long getTestimonialId() {
        return this.testimonialId;
    }

    public void setTestimonialId(Long trxTestimonial) {
        this.testimonialId = trxTestimonial;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstService)) {
            return false;
        }
        return getId() != null && getId().equals(((MstService) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstService{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", durationInHours=" + getDurationInHours() +
            ", serviceType='" + getServiceType() + "'" +
            "}";
    }
}
