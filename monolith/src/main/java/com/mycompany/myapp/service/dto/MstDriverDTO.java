package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstDriver} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstDriverDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private String contactNumber;

    private String vehicleDetails;

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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstDriverDTO)) {
            return false;
        }

        MstDriverDTO mstDriverDTO = (MstDriverDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstDriverDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstDriverDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", contactNumber='" + getContactNumber() + "'" +
            ", vehicleDetails='" + getVehicleDetails() + "'" +
            "}";
    }
}
