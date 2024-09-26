package com.mycompany.myapp.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxTestimonial} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxTestimonialDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    @Lob
    private String feedback;

    @NotNull(message = "must not be null")
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    @NotNull(message = "must not be null")
    private Instant date;

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

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxTestimonialDTO)) {
            return false;
        }

        TrxTestimonialDTO trxTestimonialDTO = (TrxTestimonialDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxTestimonialDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxTestimonialDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", feedback='" + getFeedback() + "'" +
            ", rating=" + getRating() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
