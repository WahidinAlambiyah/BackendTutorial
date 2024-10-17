package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstSupplier} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstSupplierDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private String contactInfo;

    private String address;

    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

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

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstSupplierDTO)) {
            return false;
        }

        MstSupplierDTO mstSupplierDTO = (MstSupplierDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstSupplierDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstSupplierDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", contactInfo='" + getContactInfo() + "'" +
            ", address='" + getAddress() + "'" +
            ", rating=" + getRating() +
            "}";
    }
}
