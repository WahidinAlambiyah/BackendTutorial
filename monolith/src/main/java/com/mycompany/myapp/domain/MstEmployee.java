package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MstEmployee.
 */
@Table("mst_employee")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstemployee")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstEmployee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("first_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String firstName;

    @Column("last_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastName;

    @Column("email")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @Column("phone_number")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumber;

    @Column("hire_date")
    private Instant hireDate;

    @Column("salary")
    private Long salary;

    @Column("commission_pct")
    private Long commissionPct;

    @Transient
    @JsonIgnoreProperties(value = { "tasks", "employee", "jobHistory" }, allowSetters = true)
    private Set<MstJob> mstJobs = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "mstJobs", "manager", "department", "jobHistory" }, allowSetters = true)
    private MstEmployee manager;

    @Transient
    @JsonIgnoreProperties(value = { "location", "mstEmployees", "jobHistory" }, allowSetters = true)
    private MstDepartment department;

    @Transient
    private JobHistory jobHistory;

    @Column("manager_id")
    private Long managerId;

    @Column("department_id")
    private Long departmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstEmployee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public MstEmployee firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public MstEmployee lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public MstEmployee email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public MstEmployee phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getHireDate() {
        return this.hireDate;
    }

    public MstEmployee hireDate(Instant hireDate) {
        this.setHireDate(hireDate);
        return this;
    }

    public void setHireDate(Instant hireDate) {
        this.hireDate = hireDate;
    }

    public Long getSalary() {
        return this.salary;
    }

    public MstEmployee salary(Long salary) {
        this.setSalary(salary);
        return this;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public Long getCommissionPct() {
        return this.commissionPct;
    }

    public MstEmployee commissionPct(Long commissionPct) {
        this.setCommissionPct(commissionPct);
        return this;
    }

    public void setCommissionPct(Long commissionPct) {
        this.commissionPct = commissionPct;
    }

    public Set<MstJob> getMstJobs() {
        return this.mstJobs;
    }

    public void setMstJobs(Set<MstJob> mstJobs) {
        if (this.mstJobs != null) {
            this.mstJobs.forEach(i -> i.setEmployee(null));
        }
        if (mstJobs != null) {
            mstJobs.forEach(i -> i.setEmployee(this));
        }
        this.mstJobs = mstJobs;
    }

    public MstEmployee mstJobs(Set<MstJob> mstJobs) {
        this.setMstJobs(mstJobs);
        return this;
    }

    public MstEmployee addMstJob(MstJob mstJob) {
        this.mstJobs.add(mstJob);
        mstJob.setEmployee(this);
        return this;
    }

    public MstEmployee removeMstJob(MstJob mstJob) {
        this.mstJobs.remove(mstJob);
        mstJob.setEmployee(null);
        return this;
    }

    public MstEmployee getManager() {
        return this.manager;
    }

    public void setManager(MstEmployee mstEmployee) {
        this.manager = mstEmployee;
        this.managerId = mstEmployee != null ? mstEmployee.getId() : null;
    }

    public MstEmployee manager(MstEmployee mstEmployee) {
        this.setManager(mstEmployee);
        return this;
    }

    public MstDepartment getDepartment() {
        return this.department;
    }

    public void setDepartment(MstDepartment mstDepartment) {
        this.department = mstDepartment;
        this.departmentId = mstDepartment != null ? mstDepartment.getId() : null;
    }

    public MstEmployee department(MstDepartment mstDepartment) {
        this.setDepartment(mstDepartment);
        return this;
    }

    public JobHistory getJobHistory() {
        return this.jobHistory;
    }

    public void setJobHistory(JobHistory jobHistory) {
        if (this.jobHistory != null) {
            this.jobHistory.setEmployee(null);
        }
        if (jobHistory != null) {
            jobHistory.setEmployee(this);
        }
        this.jobHistory = jobHistory;
    }

    public MstEmployee jobHistory(JobHistory jobHistory) {
        this.setJobHistory(jobHistory);
        return this;
    }

    public Long getManagerId() {
        return this.managerId;
    }

    public void setManagerId(Long mstEmployee) {
        this.managerId = mstEmployee;
    }

    public Long getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Long mstDepartment) {
        this.departmentId = mstDepartment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstEmployee)) {
            return false;
        }
        return getId() != null && getId().equals(((MstEmployee) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstEmployee{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", hireDate='" + getHireDate() + "'" +
            ", salary=" + getSalary() +
            ", commissionPct=" + getCommissionPct() +
            "}";
    }
}
