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
 * A Country.
 */
@Table("country")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "country")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Country implements Serializable {

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
    @JsonIgnoreProperties(value = { "cities", "country" }, allowSetters = true)
    private Set<Province> provinces = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "countries" }, allowSetters = true)
    private Region region;

    @Column("region_id")
    private Long regionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Country id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Country name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnm49Code() {
        return this.unm49Code;
    }

    public Country unm49Code(String unm49Code) {
        this.setUnm49Code(unm49Code);
        return this;
    }

    public void setUnm49Code(String unm49Code) {
        this.unm49Code = unm49Code;
    }

    public String getIsoAlpha2Code() {
        return this.isoAlpha2Code;
    }

    public Country isoAlpha2Code(String isoAlpha2Code) {
        this.setIsoAlpha2Code(isoAlpha2Code);
        return this;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public Set<Province> getProvinces() {
        return this.provinces;
    }

    public void setProvinces(Set<Province> provinces) {
        if (this.provinces != null) {
            this.provinces.forEach(i -> i.setCountry(null));
        }
        if (provinces != null) {
            provinces.forEach(i -> i.setCountry(this));
        }
        this.provinces = provinces;
    }

    public Country provinces(Set<Province> provinces) {
        this.setProvinces(provinces);
        return this;
    }

    public Country addProvince(Province province) {
        this.provinces.add(province);
        province.setCountry(this);
        return this;
    }

    public Country removeProvince(Province province) {
        this.provinces.remove(province);
        province.setCountry(null);
        return this;
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(Region region) {
        this.region = region;
        this.regionId = region != null ? region.getId() : null;
    }

    public Country region(Region region) {
        this.setRegion(region);
        return this;
    }

    public Long getRegionId() {
        return this.regionId;
    }

    public void setRegionId(Long region) {
        this.regionId = region;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        return getId() != null && getId().equals(((Country) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Country{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            "}";
    }
}
