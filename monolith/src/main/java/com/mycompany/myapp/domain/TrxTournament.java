package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.TournamentStatus;
import com.mycompany.myapp.domain.enumeration.TournamentType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrxTournament.
 */
@Table("trx_tournament")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "trxtournament")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxTournament implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column("type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TournamentType type;

    @Column("prize_amount")
    private BigDecimal prizeAmount;

    @NotNull(message = "must not be null")
    @Column("start_date")
    private Instant startDate;

    @NotNull(message = "must not be null")
    @Column("end_date")
    private Instant endDate;

    @Column("location")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String location;

    @Column("max_participants")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer maxParticipants;

    @Column("status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TournamentStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "tournaments", "service", "testimonial" }, allowSetters = true)
    private TrxEvent event;

    @Column("event_id")
    private Long eventId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TrxTournament id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TrxTournament name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TournamentType getType() {
        return this.type;
    }

    public TrxTournament type(TournamentType type) {
        this.setType(type);
        return this;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public BigDecimal getPrizeAmount() {
        return this.prizeAmount;
    }

    public TrxTournament prizeAmount(BigDecimal prizeAmount) {
        this.setPrizeAmount(prizeAmount);
        return this;
    }

    public void setPrizeAmount(BigDecimal prizeAmount) {
        this.prizeAmount = prizeAmount != null ? prizeAmount.stripTrailingZeros() : null;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public TrxTournament startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public TrxTournament endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return this.location;
    }

    public TrxTournament location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxParticipants() {
        return this.maxParticipants;
    }

    public TrxTournament maxParticipants(Integer maxParticipants) {
        this.setMaxParticipants(maxParticipants);
        return this;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public TournamentStatus getStatus() {
        return this.status;
    }

    public TrxTournament status(TournamentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public TrxEvent getEvent() {
        return this.event;
    }

    public void setEvent(TrxEvent trxEvent) {
        this.event = trxEvent;
        this.eventId = trxEvent != null ? trxEvent.getId() : null;
    }

    public TrxTournament event(TrxEvent trxEvent) {
        this.setEvent(trxEvent);
        return this;
    }

    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long trxEvent) {
        this.eventId = trxEvent;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrxTournament)) {
            return false;
        }
        return getId() != null && getId().equals(((TrxTournament) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxTournament{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", prizeAmount=" + getPrizeAmount() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", location='" + getLocation() + "'" +
            ", maxParticipants=" + getMaxParticipants() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
