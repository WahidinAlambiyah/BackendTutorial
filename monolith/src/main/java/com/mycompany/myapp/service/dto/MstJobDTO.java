package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstJob} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstJobDTO implements Serializable {

    private Long id;

    private String jobTitle;

    private Long minSalary;

    private Long maxSalary;

    private Set<MstTaskDTO> tasks = new HashSet<>();

    private MstEmployeeDTO employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Long minSalary) {
        this.minSalary = minSalary;
    }

    public Long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Long maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Set<MstTaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(Set<MstTaskDTO> tasks) {
        this.tasks = tasks;
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
        if (!(o instanceof MstJobDTO)) {
            return false;
        }

        MstJobDTO mstJobDTO = (MstJobDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstJobDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstJobDTO{" +
            "id=" + getId() +
            ", jobTitle='" + getJobTitle() + "'" +
            ", minSalary=" + getMinSalary() +
            ", maxSalary=" + getMaxSalary() +
            ", tasks=" + getTasks() +
            ", employee=" + getEmployee() +
            "}";
    }
}
