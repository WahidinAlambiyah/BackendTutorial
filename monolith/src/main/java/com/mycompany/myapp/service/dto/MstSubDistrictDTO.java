package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstSubDistrict} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstSubDistrictDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private String unm49Code;

    private String isoAlpha2Code;

    private MstDistrictDTO district;

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

    public String getUnm49Code() {
        return unm49Code;
    }

    public void setUnm49Code(String unm49Code) {
        this.unm49Code = unm49Code;
    }

    public String getIsoAlpha2Code() {
        return isoAlpha2Code;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public MstDistrictDTO getDistrict() {
        return district;
    }

    public void setDistrict(MstDistrictDTO district) {
        this.district = district;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstSubDistrictDTO)) {
            return false;
        }

        MstSubDistrictDTO mstSubDistrictDTO = (MstSubDistrictDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstSubDistrictDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstSubDistrictDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            ", district=" + getDistrict() +
            "}";
    }
}
