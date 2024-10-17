package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstLoyaltyProgram} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstLoyaltyProgramDTO implements Serializable {

    private Long id;

    private Integer pointsEarned;

    private String membershipTier;

    private MstCustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public MstCustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(MstCustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstLoyaltyProgramDTO)) {
            return false;
        }

        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = (MstLoyaltyProgramDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstLoyaltyProgramDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstLoyaltyProgramDTO{" +
            "id=" + getId() +
            ", pointsEarned=" + getPointsEarned() +
            ", membershipTier='" + getMembershipTier() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
