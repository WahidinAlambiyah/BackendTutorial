package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.Language;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.JobHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JobHistoryDTO implements Serializable {

    private Long id;

    private Instant startDate;

    private Instant endDate;

    private Language language;

    private MstJobDTO job;

    private MstDepartmentDTO department;

    private MstEmployeeDTO employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public MstJobDTO getJob() {
        return job;
    }

    public void setJob(MstJobDTO job) {
        this.job = job;
    }

    public MstDepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(MstDepartmentDTO department) {
        this.department = department;
    }

    public MstEmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(MstEmployeeDTO employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobHistoryDTO)) {
            return false;
        }

        JobHistoryDTO jobHistoryDTO = (JobHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, jobHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JobHistoryDTO{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", language='" + getLanguage() + "'" +
            ", job=" + getJob() +
            ", department=" + getDepartment() +
            ", employee=" + getEmployee() +
            "}";
    }
}
