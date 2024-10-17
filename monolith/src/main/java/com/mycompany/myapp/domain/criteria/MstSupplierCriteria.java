package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.MstSupplier} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.MstSupplierResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /mst-suppliers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstSupplierCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter contactInfo;

    private StringFilter address;

    private IntegerFilter rating;

    private Boolean distinct;

    public MstSupplierCriteria() {}

    public MstSupplierCriteria(MstSupplierCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.contactInfo = other.optionalContactInfo().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.rating = other.optionalRating().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MstSupplierCriteria copy() {
        return new MstSupplierCriteria(this);
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

    public StringFilter getContactInfo() {
        return contactInfo;
    }

    public Optional<StringFilter> optionalContactInfo() {
        return Optional.ofNullable(contactInfo);
    }

    public StringFilter contactInfo() {
        if (contactInfo == null) {
            setContactInfo(new StringFilter());
        }
        return contactInfo;
    }

    public void setContactInfo(StringFilter contactInfo) {
        this.contactInfo = contactInfo;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public IntegerFilter getRating() {
        return rating;
    }

    public Optional<IntegerFilter> optionalRating() {
        return Optional.ofNullable(rating);
    }

    public IntegerFilter rating() {
        if (rating == null) {
            setRating(new IntegerFilter());
        }
        return rating;
    }

    public void setRating(IntegerFilter rating) {
        this.rating = rating;
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
        final MstSupplierCriteria that = (MstSupplierCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(contactInfo, that.contactInfo) &&
            Objects.equals(address, that.address) &&
            Objects.equals(rating, that.rating) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contactInfo, address, rating, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstSupplierCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalContactInfo().map(f -> "contactInfo=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalRating().map(f -> "rating=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
