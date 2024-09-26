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
 * A MstCountry.
 */
@Table("mst_country")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstcountry")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstCountry implements Serializable {

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
    private Set<MstProvince> provinces = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "countries" }, allowSetters = true)
    private MstRegion region;

    @Column("region_id")
    private Long regionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstCountry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MstCountry name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnm49Code() {
        return this.unm49Code;
    }

    public MstCountry unm49Code(String unm49Code) {
        this.setUnm49Code(unm49Code);
        return this;
    }

    public void setUnm49Code(String unm49Code) {
        this.unm49Code = unm49Code;
    }

    public String getIsoAlpha2Code() {
        return this.isoAlpha2Code;
    }

    public MstCountry isoAlpha2Code(String isoAlpha2Code) {
        this.setIsoAlpha2Code(isoAlpha2Code);
        return this;
    }

    public void setIsoAlpha2Code(String isoAlpha2Code) {
        this.isoAlpha2Code = isoAlpha2Code;
    }

    public Set<MstProvince> getProvinces() {
        return this.provinces;
    }

    public void setProvinces(Set<MstProvince> mstProvinces) {
        if (this.provinces != null) {
            this.provinces.forEach(i -> i.setCountry(null));
        }
        if (mstProvinces != null) {
            mstProvinces.forEach(i -> i.setCountry(this));
        }
        this.provinces = mstProvinces;
    }

    public MstCountry provinces(Set<MstProvince> mstProvinces) {
        this.setProvinces(mstProvinces);
        return this;
    }

    public MstCountry addProvince(MstProvince mstProvince) {
        this.provinces.add(mstProvince);
        mstProvince.setCountry(this);
        return this;
    }

    public MstCountry removeProvince(MstProvince mstProvince) {
        this.provinces.remove(mstProvince);
        mstProvince.setCountry(null);
        return this;
    }

    public MstRegion getRegion() {
        return this.region;
    }

    public void setRegion(MstRegion mstRegion) {
        this.region = mstRegion;
        this.regionId = mstRegion != null ? mstRegion.getId() : null;
    }

    public MstCountry region(MstRegion mstRegion) {
        this.setRegion(mstRegion);
        return this;
    }

    public Long getRegionId() {
        return this.regionId;
    }

    public void setRegionId(Long mstRegion) {
        this.regionId = mstRegion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstCountry)) {
            return false;
        }
        return getId() != null && getId().equals(((MstCountry) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstCountry{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", unm49Code='" + getUnm49Code() + "'" +
            ", isoAlpha2Code='" + getIsoAlpha2Code() + "'" +
            "}";
    }
}
