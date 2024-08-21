package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A SubDistrict.
 */
@Table("sub_district")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "subdistrict")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubDistrict implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column("unm_49_code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String unm49Code;

    @Column("iso_alpha_2_code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String isoAlpha2Code;

    @Transient
    @JsonIgnoreProperties(value = { "subDistrict" }, allowSetters = true)
    private Set<PostalCode> postalCodes = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "subDistricts", "city" }, allowSetters = true)
    private District district;

    @Column("district_id")
    private Long districtId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SubDistrict id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SubDistrict name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnm49Code() {
        return this.unm49Code;
    }

    public SubDistrict unm49Code(String unm49Code) {
        this.setUnm49Code(unm49Code);
        return this;
    }

    public void setUnm49Code(String unm49Code) {
        this.unm49Code = unm49Code;
    }

    public String getIsoAlpha2Code() {
        return this.isoAlpha2Code;
    }

    public SubDistrict isoAlpha2Code(String isoAlpha2Code) {
        this.setIsoAlpha2Code(isoAlpha2Code);
        return this;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public Set<PostalCode> getPostalCodes() {
        return this.postalCodes;
    }

    public void setPostalCodes(Set<PostalCode> postalCodes) {
        if (this.postalCodes != null) {
            this.postalCodes.forEach(i -> i.setSubDistrict(null));
        }
        if (postalCodes != null) {
            postalCodes.forEach(i -> i.setSubDistrict(this));
        }
        this.postalCodes = postalCodes;
    }

    public SubDistrict postalCodes(Set<PostalCode> postalCodes) {
        this.setPostalCodes(postalCodes);
        return this;
    }

    public SubDistrict addPostalCode(PostalCode postalCode) {
        this.postalCodes.add(postalCode);
        postalCode.setSubDistrict(this);
        return this;
    }

    public SubDistrict removePostalCode(PostalCode postalCode) {
        this.postalCodes.remove(postalCode);
        postalCode.setSubDistrict(null);
        return this;
    }

    public District getDistrict() {
        return this.district;
    }

    public void setDistrict(District district) {
        this.district = district;
        this.districtId = district != null ? district.getId() : null;
    }

    public SubDistrict district(District district) {
        this.setDistrict(district);
        return this;
    }

    public Long getDistrictId() {
        return this.districtId;
    }

    public void setDistrictId(Long district) {
        this.districtId = district;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubDistrict)) {
            return false;
        }
        return getId() != null && getId().equals(((SubDistrict) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubDistrict{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            "}";
    }
}
