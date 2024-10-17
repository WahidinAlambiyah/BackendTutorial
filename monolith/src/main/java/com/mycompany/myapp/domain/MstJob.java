package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MstJob.
 */
@Table("mst_job")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstjob")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("job_title")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String jobTitle;

    @Column("min_salary")
    private Long minSalary;

    @Column("max_salary")
    private Long maxSalary;

    @Transient
    @JsonIgnoreProperties(value = { "jobs" }, allowSetters = true)
    private Set<MstTask> tasks = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "mstJobs", "manager", "department", "mstDepartment", "jobHistory" }, allowSetters = true)
    private MstEmployee employee;

    @Transient
    private JobHistory jobHistory;

    @Column("employee_id")
    private Long employeeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstJob id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public MstJob jobTitle(String jobTitle) {
        this.setJobTitle(jobTitle);
        return this;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Long getMinSalary() {
        return this.minSalary;
    }

    public MstJob minSalary(Long minSalary) {
        this.setMinSalary(minSalary);
        return this;
    }

    public void setMinSalary(Long minSalary) {
        this.minSalary = minSalary;
    }

    public Long getMaxSalary() {
        return this.maxSalary;
    }

    public MstJob maxSalary(Long maxSalary) {
        this.setMaxSalary(maxSalary);
        return this;
    }

    public void setMaxSalary(Long maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Set<MstTask> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<MstTask> mstTasks) {
        this.tasks = mstTasks;
    }

    public MstJob tasks(Set<MstTask> mstTasks) {
        this.setTasks(mstTasks);
        return this;
    }

    public MstJob addTask(MstTask mstTask) {
        this.tasks.add(mstTask);
        return this;
    }

    public MstJob removeTask(MstTask mstTask) {
        this.tasks.remove(mstTask);
        return this;
    }

    public MstEmployee getEmployee() {
        return this.employee;
    }

    public void setEmployee(MstEmployee mstEmployee) {
        this.employee = mstEmployee;
        this.employeeId = mstEmployee != null ? mstEmployee.getId() : null;
    }

    public MstJob employee(MstEmployee mstEmployee) {
        this.setEmployee(mstEmployee);
        return this;
    }

    public JobHistory getJobHistory() {
        return this.jobHistory;
    }

    public void setJobHistory(JobHistory jobHistory) {
        if (this.jobHistory != null) {
            this.jobHistory.setJob(null);
        }
        if (jobHistory != null) {
            jobHistory.setJob(this);
        }
        this.jobHistory = jobHistory;
    }

    public MstJob jobHistory(JobHistory jobHistory) {
        this.setJobHistory(jobHistory);
        return this;
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
        if (!(o instanceof MstJob)) {
            return false;
        }
        return getId() != null && getId().equals(((MstJob) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstJob{" +
            "id=" + getId() +
            ", jobTitle='" + getJobTitle() + "'" +
            ", minSalary=" + getMinSalary() +
            ", maxSalary=" + getMaxSalary() +
            "}";
    }
}
