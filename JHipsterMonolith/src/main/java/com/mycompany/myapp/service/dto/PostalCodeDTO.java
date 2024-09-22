package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.PostalCode} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostalCodeDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String code;

    private SubDistrictDTO subDistrict;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SubDistrictDTO getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(SubDistrictDTO subDistrict) {
        this.subDistrict = subDistrict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostalCodeDTO)) {
            return false;
        }

        PostalCodeDTO postalCodeDTO = (PostalCodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, postalCodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostalCodeDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", subDistrict=" + getSubDistrict() +
            "}";
    }
}
