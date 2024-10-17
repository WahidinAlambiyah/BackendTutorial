package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.MstDriver} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.MstDriverResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /mst-drivers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstDriverCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter contactNumber;

    private StringFilter vehicleDetails;

    private Boolean distinct;

    public MstDriverCriteria() {}

    public MstDriverCriteria(MstDriverCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.contactNumber = other.optionalContactNumber().map(StringFilter::copy).orElse(null);
        this.vehicleDetails = other.optionalVehicleDetails().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MstDriverCriteria copy() {
        return new MstDriverCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getContactNumber() {
        return contactNumber;
    }

    public Optional<StringFilter> optionalContactNumber() {
        return Optional.ofNullable(contactNumber);
    }

    public StringFilter contactNumber() {
        if (contactNumber == null) {
            setContactNumber(new StringFilter());
        }
        return contactNumber;
    }

    public void setContactNumber(StringFilter contactNumber) {
        this.contactNumber = contactNumber;
    }

    public StringFilter getVehicleDetails() {
        return vehicleDetails;
    }

    public Optional<StringFilter> optionalVehicleDetails() {
        return Optional.ofNullable(vehicleDetails);
    }

    public StringFilter vehicleDetails() {
        if (vehicleDetails == null) {
            setVehicleDetails(new StringFilter());
        }
        return vehicleDetails;
    }

    public void setVehicleDetails(StringFilter vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MstDriverCriteria that = (MstDriverCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(contactNumber, that.contactNumber) &&
            Objects.equals(vehicleDetails, that.vehicleDetails) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contactNumber, vehicleDetails, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstDriverCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalContactNumber().map(f -> "contactNumber=" + f + ", ").orElse("") +
            optionalVehicleDetails().map(f -> "vehicleDetails=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
