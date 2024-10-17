package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstBrand} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstBrandDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private String logo;

    private String description;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstBrandDTO)) {
            return false;
        }

        MstBrandDTO mstBrandDTO = (MstBrandDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstBrandDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstBrandDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", logo='" + getLogo() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
