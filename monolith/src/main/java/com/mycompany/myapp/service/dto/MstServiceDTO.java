package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.ServiceType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstService} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstServiceDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    @Lob
    private String description;

    private BigDecimal price;

    private Integer durationInHours;

    private ServiceType serviceType;

    private TrxTestimonialDTO testimonial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationInHours() {
        return durationInHours;
    }

    public void setDurationInHours(Integer durationInHours) {
        this.durationInHours = durationInHours;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
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
        if (!(o instanceof MstServiceDTO)) {
            return false;
        }

        MstServiceDTO mstServiceDTO = (MstServiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstServiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstServiceDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", durationInHours=" + getDurationInHours() +
            ", serviceType='" + getServiceType() + "'" +
            ", testimonial=" + getTestimonial() +
            "}";
    }
}
