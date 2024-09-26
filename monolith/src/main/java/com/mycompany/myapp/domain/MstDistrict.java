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
 * A MstDistrict.
 */
@Table("mst_district")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstdistrict")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstDistrict implements Serializable {

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
    @JsonIgnoreProperties(value = { "postalCodes", "district" }, allowSetters = true)
    private Set<MstSubDistrict> subDistricts = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "districts", "province" }, allowSetters = true)
    private MstCity city;

    @Column("city_id")
    private Long cityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstDistrict id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MstDistrict name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnm49Code() {
        return this.unm49Code;
    }

    public MstDistrict unm49Code(String unm49Code) {
        this.setUnm49Code(unm49Code);
        return this;
    }

    public void setUnm49Code(String unm49Code) {
        this.unm49Code = unm49Code;
    }

    public String getIsoAlpha2Code() {
        return this.isoAlpha2Code;
    }

    public MstDistrict isoAlpha2Code(String isoAlpha2Code) {
        this.setIsoAlpha2Code(isoAlpha2Code);
        return this;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public Set<MstSubDistrict> getSubDistricts() {
        return this.subDistricts;
    }

    public void setSubDistricts(Set<MstSubDistrict> mstSubDistricts) {
        if (this.subDistricts != null) {
            this.subDistricts.forEach(i -> i.setDistrict(null));
        }
        if (mstSubDistricts != null) {
            mstSubDistricts.forEach(i -> i.setDistrict(this));
        }
        this.subDistricts = mstSubDistricts;
    }

    public MstDistrict subDistricts(Set<MstSubDistrict> mstSubDistricts) {
        this.setSubDistricts(mstSubDistricts);
        return this;
    }

    public MstDistrict addSubDistrict(MstSubDistrict mstSubDistrict) {
        this.subDistricts.add(mstSubDistrict);
        mstSubDistrict.setDistrict(this);
        return this;
    }

    public MstDistrict removeSubDistrict(MstSubDistrict mstSubDistrict) {
        this.subDistricts.remove(mstSubDistrict);
        mstSubDistrict.setDistrict(null);
        return this;
    }

    public MstCity getCity() {
        return this.city;
    }

    public void setCity(MstCity mstCity) {
        this.city = mstCity;
        this.cityId = mstCity != null ? mstCity.getId() : null;
    }

    public MstDistrict city(MstCity mstCity) {
        this.setCity(mstCity);
        return this;
    }

    public Long getCityId() {
        return this.cityId;
    }

    public void setCityId(Long mstCity) {
        this.cityId = mstCity;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstDistrict)) {
            return false;
        }
        return getId() != null && getId().equals(((MstDistrict) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstDistrict{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            "}";
    }
}
