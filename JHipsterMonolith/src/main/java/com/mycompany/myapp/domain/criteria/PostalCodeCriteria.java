package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.PostalCode} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.PostalCodeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /postal-codes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostalCodeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private LongFilter subDistrictId;

    private Boolean distinct;

    public PostalCodeCriteria() {}

    public PostalCodeCriteria(PostalCodeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.subDistrictId = other.optionalSubDistrictId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PostalCodeCriteria copy() {
        return new PostalCodeCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public LongFilter getSubDistrictId() {
        return subDistrictId;
    }

    public Optional<LongFilter> optionalSubDistrictId() {
        return Optional.ofNullable(subDistrictId);
    }

    public LongFilter subDistrictId() {
        if (subDistrictId == null) {
            setSubDistrictId(new LongFilter());
        }
        return subDistrictId;
    }

    public void setSubDistrictId(LongFilter subDistrictId) {
        this.subDistrictId = subDistrictId;
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
        final PostalCodeCriteria that = (PostalCodeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(subDistrictId, that.subDistrictId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, subDistrictId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostalCodeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalSubDistrictId().map(f -> "subDistrictId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
