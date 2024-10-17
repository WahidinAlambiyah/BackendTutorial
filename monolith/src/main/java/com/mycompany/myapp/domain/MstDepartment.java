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
 * A MstDepartment.
 */
@Table("mst_department")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstdepartment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstDepartment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("department_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String departmentName;

    @Transient
    private Location location;

    @Transient
    @JsonIgnoreProperties(value = { "mstJobs", "manager", "department", "mstDepartment", "jobHistory" }, allowSetters = true)
    private Set<MstEmployee> mstEmployees = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "mstJobs", "manager", "department", "mstDepartment", "jobHistory" }, allowSetters = true)
    private Set<MstEmployee> employees = new HashSet<>();

    @Transient
    private JobHistory jobHistory;

    @Column("location_id")
    private Long locationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstDepartment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public MstDepartment departmentName(String departmentName) {
        this.setDepartmentName(departmentName);
        return this;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.locationId = location != null ? location.getId() : null;
    }

    public MstDepartment location(Location location) {
        this.setLocation(location);
        return this;
    }

    public Set<MstEmployee> getMstEmployees() {
        return this.mstEmployees;
    }

    public void setMstEmployees(Set<MstEmployee> mstEmployees) {
        if (this.mstEmployees != null) {
            this.mstEmployees.forEach(i -> i.setDepartment(null));
        }
        if (mstEmployees != null) {
            mstEmployees.forEach(i -> i.setDepartment(this));
        }
        this.mstEmployees = mstEmployees;
    }

    public MstDepartment mstEmployees(Set<MstEmployee> mstEmployees) {
        this.setMstEmployees(mstEmployees);
        return this;
    }

    public MstDepartment addMstEmployee(MstEmployee mstEmployee) {
        this.mstEmployees.add(mstEmployee);
        mstEmployee.setDepartment(this);
        return this;
    }

    public MstDepartment removeMstEmployee(MstEmployee mstEmployee) {
        this.mstEmployees.remove(mstEmployee);
        mstEmployee.setDepartment(null);
        return this;
    }

    public Set<MstEmployee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<MstEmployee> mstEmployees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setMstDepartment(null));
        }
        if (mstEmployees != null) {
            mstEmployees.forEach(i -> i.setMstDepartment(this));
        }
        this.employees = mstEmployees;
    }

    public MstDepartment employees(Set<MstEmployee> mstEmployees) {
        this.setEmployees(mstEmployees);
        return this;
    }

    public MstDepartment addEmployee(MstEmployee mstEmployee) {
        this.employees.add(mstEmployee);
        mstEmployee.setMstDepartment(this);
        return this;
    }

    public MstDepartment removeEmployee(MstEmployee mstEmployee) {
        this.employees.remove(mstEmployee);
        mstEmployee.setMstDepartment(null);
        return this;
    }

    public JobHistory getJobHistory() {
        return this.jobHistory;
    }

    public void setJobHistory(JobHistory jobHistory) {
        if (this.jobHistory != null) {
            this.jobHistory.setDepartment(null);
        }
        if (jobHistory != null) {
            jobHistory.setDepartment(this);
        }
        this.jobHistory = jobHistory;
    }

    public MstDepartment jobHistory(JobHistory jobHistory) {
        this.setJobHistory(jobHistory);
        return this;
    }

    public Long getLocationId() {
        return this.locationId;
    }

    public void setLocationId(Long location) {
        this.locationId = location;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstDepartment)) {
            return false;
        }
        return getId() != null && getId().equals(((MstDepartment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstDepartment{" +
            "id=" + getId() +
            ", departmentName='" + getDepartmentName() + "'" +
            "}";
    }
}
