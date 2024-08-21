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
 * A City.
 */
@Table("city")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "city")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class City implements Serializable {

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
    @JsonIgnoreProperties(value = { "subDistricts", "city" }, allowSetters = true)
    private Set<District> districts = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "cities", "country" }, allowSetters = true)
    private Province province;

    @Column("province_id")
    private Long provinceId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public City id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public City name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnm49Code() {
        return this.unm49Code;
    }

    public City unm49Code(String unm49Code) {
        this.setUnm49Code(unm49Code);
        return this;
    }

    public void setUnm49Code(String unm49Code) {
        this.unm49Code = unm49Code;
    }

    public String getIsoAlpha2Code() {
        return this.isoAlpha2Code;
    }

    public City isoAlpha2Code(String isoAlpha2Code) {
        this.setIsoAlpha2Code(isoAlpha2Code);
        return this;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public Set<District> getDistricts() {
        return this.districts;
    }

    public void setDistricts(Set<District> districts) {
        if (this.districts != null) {
            this.districts.forEach(i -> i.setCity(null));
        }
        if (districts != null) {
            districts.forEach(i -> i.setCity(this));
        }
        this.districts = districts;
    }

    public City districts(Set<District> districts) {
        this.setDistricts(districts);
        return this;
    }

    public City addDistrict(District district) {
        this.districts.add(district);
        district.setCity(this);
        return this;
    }

    public City removeDistrict(District district) {
        this.districts.remove(district);
        district.setCity(null);
        return this;
    }

    public Province getProvince() {
        return this.province;
    }

    public void setProvince(Province province) {
        this.province = province;
        this.provinceId = province != null ? province.getId() : null;
    }

    public City province(Province province) {
        this.setProvince(province);
        return this;
    }

    public Long getProvinceId() {
        return this.provinceId;
    }

    public void setProvinceId(Long province) {
        this.provinceId = province;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof City)) {
            return false;
        }
        return getId() != null && getId().equals(((City) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "City{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            "}";
    }
}
