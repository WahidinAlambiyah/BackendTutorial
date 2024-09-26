package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstCountry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstCountryDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private String unm49Code;

    private String isoAlpha2Code;

    private MstRegionDTO region;

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

    public MstRegionDTO getRegion() {
        return region;
    }

    public void setRegion(MstRegionDTO region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstCountryDTO)) {
            return false;
        }

        MstCountryDTO mstCountryDTO = (MstCountryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstCountryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstCountryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            ", region=" + getRegion() +
            "}";
    }
}
