package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstPostalCode} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstPostalCodeDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String code;

    private MstSubDistrictDTO subDistrict;

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

    public MstSubDistrictDTO getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(MstSubDistrictDTO subDistrict) {
        this.subDistrict = subDistrict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstPostalCodeDTO)) {
            return false;
        }

        MstPostalCodeDTO mstPostalCodeDTO = (MstPostalCodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstPostalCodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstPostalCodeDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", subDistrict=" + getSubDistrict() +
            "}";
    }
}
