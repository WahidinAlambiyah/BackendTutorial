package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.Language;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A JobHistory.
 */
@Table("job_history")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "jobhistory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JobHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("start_date")
    private Instant startDate;

    @Column("end_date")
    private Instant endDate;

    @Column("language")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Language language;

    @Transient
    private MstJob job;

    @Transient
    private MstDepartment department;

    @Transient
    private MstEmployee employee;

    @Column("job_id")
    private Long jobId;

    @Column("department_id")
    private Long departmentId;

    @Column("employee_id")
    private Long employeeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public JobHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public JobHistory startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public JobHistory endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Language getLanguage() {
        return this.language;
    }

    public JobHistory language(Language language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public MstJob getJob() {
        return this.job;
    }

    public void setJob(MstJob mstJob) {
        this.job = mstJob;
        this.jobId = mstJob != null ? mstJob.getId() : null;
    }

    public JobHistory job(MstJob mstJob) {
        this.setJob(mstJob);
        return this;
    }

    public MstDepartment getDepartment() {
        return this.department;
    }

    public void setDepartment(MstDepartment mstDepartment) {
        this.department = mstDepartment;
        this.departmentId = mstDepartment != null ? mstDepartment.getId() : null;
    }

    public JobHistory department(MstDepartment mstDepartment) {
        this.setDepartment(mstDepartment);
        return this;
    }

    public MstEmployee getEmployee() {
        return this.employee;
    }

    public void setEmployee(MstEmployee mstEmployee) {
        this.employee = mstEmployee;
        this.employeeId = mstEmployee != null ? mstEmployee.getId() : null;
    }

    public JobHistory employee(MstEmployee mstEmployee) {
        this.setEmployee(mstEmployee);
        return this;
    }

    public Long getJobId() {
        return this.jobId;
    }

    public void setJobId(Long mstJob) {
        this.jobId = mstJob;
    }

    public Long getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Long mstDepartment) {
        this.departmentId = mstDepartment;
    }

    public Long getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(Long mstEmployee) {
        this.employeeId = mstEmployee;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((JobHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JobHistory{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
