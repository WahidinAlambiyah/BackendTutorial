package com.mycompany.myapp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.MstLoyaltyProgram} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.MstLoyaltyProgramResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /mst-loyalty-programs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstLoyaltyProgramCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter pointsEarned;

    private StringFilter membershipTier;

    private LongFilter customerId;

    private Boolean distinct;

    public MstLoyaltyProgramCriteria() {}

    public MstLoyaltyProgramCriteria(MstLoyaltyProgramCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.pointsEarned = other.optionalPointsEarned().map(IntegerFilter::copy).orElse(null);
        this.membershipTier = other.optionalMembershipTier().map(StringFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MstLoyaltyProgramCriteria copy() {
        return new MstLoyaltyProgramCriteria(this);
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

    public IntegerFilter getPointsEarned() {
        return pointsEarned;
    }

    public Optional<IntegerFilter> optionalPointsEarned() {
        return Optional.ofNullable(pointsEarned);
    }

    public IntegerFilter pointsEarned() {
        if (pointsEarned == null) {
            setPointsEarned(new IntegerFilter());
        }
        return pointsEarned;
    }

    public void setPointsEarned(IntegerFilter pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public StringFilter getMembershipTier() {
        return membershipTier;
    }

    public Optional<StringFilter> optionalMembershipTier() {
        return Optional.ofNullable(membershipTier);
    }

    public StringFilter membershipTier() {
        if (membershipTier == null) {
            setMembershipTier(new StringFilter());
        }
        return membershipTier;
    }

    public void setMembershipTier(StringFilter membershipTier) {
        this.membershipTier = membershipTier;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
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
        final MstLoyaltyProgramCriteria that = (MstLoyaltyProgramCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(pointsEarned, that.pointsEarned) &&
            Objects.equals(membershipTier, that.membershipTier) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pointsEarned, membershipTier, customerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstLoyaltyProgramCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPointsEarned().map(f -> "pointsEarned=" + f + ", ").orElse("") +
            optionalMembershipTier().map(f -> "membershipTier=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
