package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.MstTask} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstTaskDTO implements Serializable {

    private Long id;

    private String title;

    private String description;

    private Set<MstJobDTO> jobs = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<MstJobDTO> getJobs() {
        return jobs;
    }

    public void setJobs(Set<MstJobDTO> jobs) {
        this.jobs = jobs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstTaskDTO)) {
            return false;
        }

        MstTaskDTO mstTaskDTO = (MstTaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mstTaskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstTaskDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", jobs=" + getJobs() +
            "}";
    }
}
