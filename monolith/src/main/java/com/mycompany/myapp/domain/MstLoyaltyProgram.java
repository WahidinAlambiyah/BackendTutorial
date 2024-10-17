package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MstLoyaltyProgram.
 */
@Table("mst_loyalty_program")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstloyaltyprogram")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstLoyaltyProgram implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("points_earned")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer pointsEarned;

    @Column("membership_tier")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String membershipTier;

    @Transient
    @JsonIgnoreProperties(value = { "orders" }, allowSetters = true)
    private MstCustomer customer;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstLoyaltyProgram id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPointsEarned() {
        return this.pointsEarned;
    }

    public MstLoyaltyProgram pointsEarned(Integer pointsEarned) {
        this.setPointsEarned(pointsEarned);
        return this;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getMembershipTier() {
        return this.membershipTier;
    }

    public MstLoyaltyProgram membershipTier(String membershipTier) {
        this.setMembershipTier(membershipTier);
        return this;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public MstCustomer getCustomer() {
        return this.customer;
    }

    public void setCustomer(MstCustomer mstCustomer) {
        this.customer = mstCustomer;
        this.customerId = mstCustomer != null ? mstCustomer.getId() : null;
    }

    public MstLoyaltyProgram customer(MstCustomer mstCustomer) {
        this.setCustomer(mstCustomer);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long mstCustomer) {
        this.customerId = mstCustomer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstLoyaltyProgram)) {
            return false;
        }
        return getId() != null && getId().equals(((MstLoyaltyProgram) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstLoyaltyProgram{" +
            "id=" + getId() +
            ", pointsEarned=" + getPointsEarned() +
            ", membershipTier='" + getMembershipTier() + "'" +
            "}";
    }
}
