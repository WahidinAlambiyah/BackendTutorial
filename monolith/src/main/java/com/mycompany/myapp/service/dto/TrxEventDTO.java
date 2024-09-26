package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.EventStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxEventDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String title;

    @Lob
    private String description;

    @NotNull(message = "must not be null")
    private Instant date;

    private String location;

    private Integer capacity;

    private BigDecimal price;

    private EventStatus status;

    private MstServiceDTO service;

    private TrxTestimonialDTO testimonial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public MstServiceDTO getService() {
        return service;
    }

    public void setService(MstServiceDTO service) {
        this.service = service;
    }

    public TrxTestimonialDTO getTestimonial() {
        return testimonial;
    }

    public void setTestimonial(TrxTestimonialDTO testimonial) {
        this.testimonial = testimonial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxEventDTO)) {
            return false;
        }

        TrxEventDTO trxEventDTO = (TrxEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxEventDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", date='" + getDate() + "'" +
            ", location='" + getLocation() + "'" +
            ", capacity=" + getCapacity() +
            ", price=" + getPrice() +
            ", status='" + getStatus() + "'" +
            ", service=" + getService() +
            ", testimonial=" + getTestimonial() +
            "}";
    }
}
