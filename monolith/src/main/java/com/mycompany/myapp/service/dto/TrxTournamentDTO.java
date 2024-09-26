package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.TournamentStatus;
import com.mycompany.myapp.domain.enumeration.TournamentType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrxTournament} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxTournamentDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private TournamentType type;

    private BigDecimal prizeAmount;

    @NotNull(message = "must not be null")
    private Instant startDate;

    @NotNull(message = "must not be null")
    private Instant endDate;

    private String location;

    private Integer maxParticipants;

    private TournamentStatus status;

    private TrxEventDTO event;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TournamentType getType() {
        return type;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public BigDecimal getPrizeAmount() {
        return prizeAmount;
    }

    public void setPrizeAmount(BigDecimal prizeAmount) {
        this.prizeAmount = prizeAmount;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public TrxEventDTO getEvent() {
        return event;
    }

    public void setEvent(TrxEventDTO event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxTournamentDTO)) {
            return false;
        }

        TrxTournamentDTO trxTournamentDTO = (TrxTournamentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trxTournamentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxTournamentDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", prizeAmount=" + getPrizeAmount() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", location='" + getLocation() + "'" +
            ", maxParticipants=" + getMaxParticipants() +
            ", status='" + getStatus() + "'" +
            ", event=" + getEvent() +
            "}";
    }
}
