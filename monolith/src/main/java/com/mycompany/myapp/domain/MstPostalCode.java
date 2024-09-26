package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MstPostalCode.
 */
@Table("mst_postal_code")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstpostalcode")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstPostalCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @Transient
    @JsonIgnoreProperties(value = { "postalCodes", "district" }, allowSetters = true)
    private MstSubDistrict subDistrict;

    @Column("sub_district_id")
    private Long subDistrictId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstPostalCode id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public MstPostalCode code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MstSubDistrict getSubDistrict() {
        return this.subDistrict;
    }

    public void setSubDistrict(MstSubDistrict mstSubDistrict) {
        this.subDistrict = mstSubDistrict;
        this.subDistrictId = mstSubDistrict != null ? mstSubDistrict.getId() : null;
    }

    public MstPostalCode subDistrict(MstSubDistrict mstSubDistrict) {
        this.setSubDistrict(mstSubDistrict);
        return this;
    }

    public Long getSubDistrictId() {
        return this.subDistrictId;
    }

    public void setSubDistrictId(Long mstSubDistrict) {
        this.subDistrictId = mstSubDistrict;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstPostalCode)) {
            return false;
        }
        return getId() != null && getId().equals(((MstPostalCode) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstPostalCode{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            "}";
    }
}
