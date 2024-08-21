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
 * A Province.
 */
@Table("province")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "province")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @NotNull(message = "must not be null")
    @Column("code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @Transient
    @JsonIgnoreProperties(value = { "districts", "province" }, allowSetters = true)
    private Set<City> cities = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "provinces", "region" }, allowSetters = true)
    private Country country;

    @Column("country_id")
    private Long countryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Province id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Province name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public Province code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<City> getCities() {
        return this.cities;
    }

    public void setCities(Set<City> cities) {
        if (this.cities != null) {
            this.cities.forEach(i -> i.setProvince(null));
        }
        if (cities != null) {
            cities.forEach(i -> i.setProvince(this));
        }
        this.cities = cities;
    }

    public Province cities(Set<City> cities) {
        this.setCities(cities);
        return this;
    }

    public Province addCity(City city) {
        this.cities.add(city);
        city.setProvince(this);
        return this;
    }

    public Province removeCity(City city) {
        this.cities.remove(city);
        city.setProvince(null);
        return this;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
        this.countryId = country != null ? country.getId() : null;
    }

    public Province country(Country country) {
        this.setCountry(country);
        return this;
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public void setCountryId(Long country) {
        this.countryId = country;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Province)) {
            return false;
        }
        return getId() != null && getId().equals(((Province) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Province{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            "}";
    }
}
