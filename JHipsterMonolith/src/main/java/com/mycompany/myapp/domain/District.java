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
 * A District.
 */
@Table("district")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "district")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class District implements Serializable {

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
    private Set<SubDistrict> subDistricts = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "districts", "province" }, allowSetters = true)
    private City city;

    @Column("city_id")
    private Long cityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public District id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public District name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnm49Code() {
        return this.unm49Code;
    }

    public District unm49Code(String unm49Code) {
        this.setUnm49Code(unm49Code);
        return this;
    }

    public void setUnm49Code(String unm49Code) {
        this.unm49Code = unm49Code;
    }

    public String getIsoAlpha2Code() {
        return this.isoAlpha2Code;
    }

    public District isoAlpha2Code(String isoAlpha2Code) {
        this.setIsoAlpha2Code(isoAlpha2Code);
        return this;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public Set<SubDistrict> getSubDistricts() {
        return this.subDistricts;
    }

    public void setSubDistricts(Set<SubDistrict> subDistricts) {
        if (this.subDistricts != null) {
            this.subDistricts.forEach(i -> i.setDistrict(null));
        }
        if (subDistricts != null) {
            subDistricts.forEach(i -> i.setDistrict(this));
        }
        this.subDistricts = subDistricts;
    }

    public District subDistricts(Set<SubDistrict> subDistricts) {
        this.setSubDistricts(subDistricts);
        return this;
    }

    public District addSubDistrict(SubDistrict subDistrict) {
        this.subDistricts.add(subDistrict);
        subDistrict.setDistrict(this);
        return this;
    }

    public District removeSubDistrict(SubDistrict subDistrict) {
        this.subDistricts.remove(subDistrict);
        subDistrict.setDistrict(null);
        return this;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
        this.cityId = city != null ? city.getId() : null;
    }

    public District city(City city) {
        this.setCity(city);
        return this;
    }

    public Long getCityId() {
        return this.cityId;
    }

    public void setCityId(Long city) {
        this.cityId = city;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof District)) {
            return false;
        }
        return getId() != null && getId().equals(((District) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "District{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            "}";
    }
}
