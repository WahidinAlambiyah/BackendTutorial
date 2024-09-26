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
 * A TrxEvent.
 */
@Table("trx_event")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxevent")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxEvent implements Serializable {

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
    private Set<TrxTournament> tournaments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "testimonial", "events" }, allowSetters = true)
    private MstService service;

    @Transient
    @JsonIgnoreProperties(value = { "events", "services" }, allowSetters = true)
    private TrxTestimonial testimonial;

    @Column("service_id")
    private Long serviceId;

    @Column("testimonial_id")
    private Long testimonialId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public TrxEvent title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public TrxEvent description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDate() {
        return this.date;
    }

    public TrxEvent date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getLocation() {
        return this.location;
    }

    public TrxEvent location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public TrxEvent capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public TrxEvent price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public EventStatus getStatus() {
        return this.status;
    }

    public TrxEvent status(EventStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Set<TrxTournament> getTournaments() {
        return this.tournaments;
    }

    public void setTournaments(Set<TrxTournament> trxTournaments) {
        if (this.tournaments != null) {
            this.tournaments.forEach(i -> i.setEvent(null));
        }
        if (trxTournaments != null) {
            trxTournaments.forEach(i -> i.setEvent(this));
        }
        this.tournaments = trxTournaments;
    }

    public TrxEvent tournaments(Set<TrxTournament> trxTournaments) {
        this.setTournaments(trxTournaments);
        return this;
    }

    public TrxEvent addTournament(TrxTournament trxTournament) {
        this.tournaments.add(trxTournament);
        trxTournament.setEvent(this);
        return this;
    }

    public TrxEvent removeTournament(TrxTournament trxTournament) {
        this.tournaments.remove(trxTournament);
        trxTournament.setEvent(null);
        return this;
    }

    public MstService getService() {
        return this.service;
    }

    public void setService(MstService mstService) {
        this.service = mstService;
        this.serviceId = mstService != null ? mstService.getId() : null;
    }

    public TrxEvent service(MstService mstService) {
        this.setService(mstService);
        return this;
    }

    public TrxTestimonial getTestimonial() {
        return this.testimonial;
    }

    public void setTestimonial(TrxTestimonial trxTestimonial) {
        this.testimonial = trxTestimonial;
        this.testimonialId = trxTestimonial != null ? trxTestimonial.getId() : null;
    }

    public TrxEvent testimonial(TrxTestimonial trxTestimonial) {
        this.setTestimonial(trxTestimonial);
        return this;
    }

    public Long getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(Long mstService) {
        this.serviceId = mstService;
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
        if (!(o instanceof TrxEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxEvent{" +
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
