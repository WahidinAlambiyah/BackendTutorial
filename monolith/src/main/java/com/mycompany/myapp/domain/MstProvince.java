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
 * A MstProvince.
 */
@Table("mst_province")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstprovince")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstProvince implements Serializable {

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
    @JsonIgnoreProperties(value = { "districts", "province" }, allowSetters = true)
    private Set<MstCity> cities = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "provinces", "region" }, allowSetters = true)
    private MstCountry country;

    @Column("country_id")
    private Long countryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstProvince id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MstProvince name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnm49Code() {
        return this.unm49Code;
    }

    public MstProvince unm49Code(String unm49Code) {
        this.setUnm49Code(unm49Code);
        return this;
    }

    public void setUnm49Code(String unm49Code) {
        this.unm49Code = unm49Code;
    }

    public String getIsoAlpha2Code() {
        return this.isoAlpha2Code;
    }

    public MstProvince isoAlpha2Code(String isoAlpha2Code) {
        this.setIsoAlpha2Code(isoAlpha2Code);
        return this;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public Set<MstCity> getCities() {
        return this.cities;
    }

    public void setCities(Set<MstCity> mstCities) {
        if (this.cities != null) {
            this.cities.forEach(i -> i.setProvince(null));
        }
        if (mstCities != null) {
            mstCities.forEach(i -> i.setProvince(this));
        }
        this.cities = mstCities;
    }

    public MstProvince cities(Set<MstCity> mstCities) {
        this.setCities(mstCities);
        return this;
    }

    public MstProvince addCity(MstCity mstCity) {
        this.cities.add(mstCity);
        mstCity.setProvince(this);
        return this;
    }

    public MstProvince removeCity(MstCity mstCity) {
        this.cities.remove(mstCity);
        mstCity.setProvince(null);
        return this;
    }

    public MstCountry getCountry() {
        return this.country;
    }

    public void setCountry(MstCountry mstCountry) {
        this.country = mstCountry;
        this.countryId = mstCountry != null ? mstCountry.getId() : null;
    }

    public MstProvince country(MstCountry mstCountry) {
        this.setCountry(mstCountry);
        return this;
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public void setCountryId(Long mstCountry) {
        this.countryId = mstCountry;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstProvince)) {
            return false;
        }
        return getId() != null && getId().equals(((MstProvince) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstProvince{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            "}";
    }
}
