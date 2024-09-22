package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Country} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.CountryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /countries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CountryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter unm49Code;

    private StringFilter isoAlpha2Code;

    private LongFilter regionId;

    private Boolean distinct;

    public CountryCriteria() {}

    public CountryCriteria(CountryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.unm49Code = other.optionalUnm49Code().map(StringFilter::copy).orElse(null);
        this.isoAlpha2Code = other.optionalIsoAlpha2Code().map(StringFilter::copy).orElse(null);
        this.regionId = other.optionalRegionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CountryCriteria copy() {
        return new CountryCriteria(this);
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

    public StringFilter getUnm49Code() {
        return unm49Code;
    }

    public Optional<StringFilter> optionalUnm49Code() {
        return Optional.ofNullable(unm49Code);
    }

    public StringFilter unm49Code() {
        if (unm49Code == null) {
            setUnm49Code(new StringFilter());
        }
        return unm49Code;
    }

    public void setUnm49Code(StringFilter unm49Code) {
        this.unm49Code = unm49Code;
    }

    public StringFilter getIsoAlpha2Code() {
        return isoAlpha2Code;
    }

    public Optional<StringFilter> optionalIsoAlpha2Code() {
        return Optional.ofNullable(isoAlpha2Code);
    }

    public StringFilter isoAlpha2Code() {
        if (isoAlpha2Code == null) {
            setIsoAlpha2Code(new StringFilter());
        }
        return isoAlpha2Code;
    }

    public void setIsoAlpha2Code(StringFilter isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public LongFilter getRegionId() {
        return regionId;
    }

    public Optional<LongFilter> optionalRegionId() {
        return Optional.ofNullable(regionId);
    }

    public LongFilter regionId() {
        if (regionId == null) {
            setRegionId(new LongFilter());
        }
        return regionId;
    }

    public void setRegionId(LongFilter regionId) {
        this.regionId = regionId;
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
        final CountryCriteria that = (CountryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(unm49Code, that.unm49Code) &&
            Objects.equals(isoAlpha2Code, that.isoAlpha2Code) &&
            Objects.equals(regionId, that.regionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, unm49Code, isoAlpha2Code, regionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CountryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalUnm49Code().map(f -> "unm49Code=" + f + ", ").orElse("") +
            optionalIsoAlpha2Code().map(f -> "isoAlpha2Code=" + f + ", ").orElse("") +
            optionalRegionId().map(f -> "regionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
