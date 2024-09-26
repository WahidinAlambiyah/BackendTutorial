package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstDepartment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstDepartmentDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String departmentName;

    private LocationDTO location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstDepartmentDTO)) {
            return false;
        }

        MstDepartmentDTO mstDepartmentDTO = (MstDepartmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstDepartmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstDepartmentDTO{" +
            "id=" + getId() +
            ", departmentName='" + getDepartmentName() + "'" +
            ", location=" + getLocation() +
            "}";
    }
}
