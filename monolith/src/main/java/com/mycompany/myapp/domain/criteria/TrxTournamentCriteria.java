package com.mycompany.myapp.domain.criteria;

import com.mycompany.myapp.domain.enumeration.TournamentStatus;
import com.mycompany.myapp.domain.enumeration.TournamentType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TrxTournament} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TrxTournamentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trx-tournaments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrxTournamentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TournamentType
     */
    public static class TournamentTypeFilter extends Filter<TournamentType> {

        public TournamentTypeFilter() {}

        public TournamentTypeFilter(TournamentTypeFilter filter) {
            super(filter);
        }

        @Override
        public TournamentTypeFilter copy() {
            return new TournamentTypeFilter(this);
        }
    }

    /**
     * Class for filtering TournamentStatus
     */
    public static class TournamentStatusFilter extends Filter<TournamentStatus> {

        public TournamentStatusFilter() {}

        public TournamentStatusFilter(TournamentStatusFilter filter) {
            super(filter);
        }

        @Override
        public TournamentStatusFilter copy() {
            return new TournamentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private TournamentTypeFilter type;

    private BigDecimalFilter prizeAmount;

    private InstantFilter startDate;

    private InstantFilter endDate;

    private StringFilter location;

    private IntegerFilter maxParticipants;

    private TournamentStatusFilter status;

    private LongFilter eventId;

    private Boolean distinct;

    public TrxTournamentCriteria() {}

    public TrxTournamentCriteria(TrxTournamentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(TournamentTypeFilter::copy).orElse(null);
        this.prizeAmount = other.optionalPrizeAmount().map(BigDecimalFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(InstantFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(InstantFilter::copy).orElse(null);
        this.location = other.optionalLocation().map(StringFilter::copy).orElse(null);
        this.maxParticipants = other.optionalMaxParticipants().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(TournamentStatusFilter::copy).orElse(null);
        this.eventId = other.optionalEventId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TrxTournamentCriteria copy() {
        return new TrxTournamentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public TournamentTypeFilter getType() {
        return type;
    }

    public Optional<TournamentTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public TournamentTypeFilter type() {
        if (type == null) {
            setType(new TournamentTypeFilter());
        }
        return type;
    }

    public void setType(TournamentTypeFilter type) {
        this.type = type;
    }

    public BigDecimalFilter getPrizeAmount() {
        return prizeAmount;
    }

    public Optional<BigDecimalFilter> optionalPrizeAmount() {
        return Optional.ofNullable(prizeAmount);
    }

    public BigDecimalFilter prizeAmount() {
        if (prizeAmount == null) {
            setPrizeAmount(new BigDecimalFilter());
        }
        return prizeAmount;
    }

    public void setPrizeAmount(BigDecimalFilter prizeAmount) {
        this.prizeAmount = prizeAmount;
    }

    public InstantFilter getStartDate() {
        return startDate;
    }

    public Optional<InstantFilter> optionalStartDate() {
        return Optional.ofNullable(startDate);
    }

    public InstantFilter startDate() {
        if (startDate == null) {
            setStartDate(new InstantFilter());
        }
        return startDate;
    }

    public void setStartDate(InstantFilter startDate) {
        this.startDate = startDate;
    }

    public InstantFilter getEndDate() {
        return endDate;
    }

    public Optional<InstantFilter> optionalEndDate() {
        return Optional.ofNullable(endDate);
    }

    public InstantFilter endDate() {
        if (endDate == null) {
            setEndDate(new InstantFilter());
        }
        return endDate;
    }

    public void setEndDate(InstantFilter endDate) {
        this.endDate = endDate;
    }

    public StringFilter getLocation() {
        return location;
    }

    public Optional<StringFilter> optionalLocation() {
        return Optional.ofNullable(location);
    }

    public StringFilter location() {
        if (location == null) {
            setLocation(new StringFilter());
        }
        return location;
    }

    public void setLocation(StringFilter location) {
        this.location = location;
    }

    public IntegerFilter getMaxParticipants() {
        return maxParticipants;
    }

    public Optional<IntegerFilter> optionalMaxParticipants() {
        return Optional.ofNullable(maxParticipants);
    }

    public IntegerFilter maxParticipants() {
        if (maxParticipants == null) {
            setMaxParticipants(new IntegerFilter());
        }
        return maxParticipants;
    }

    public void setMaxParticipants(IntegerFilter maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public TournamentStatusFilter getStatus() {
        return status;
    }

    public Optional<TournamentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public TournamentStatusFilter status() {
        if (status == null) {
            setStatus(new TournamentStatusFilter());
        }
        return status;
    }

    public void setStatus(TournamentStatusFilter status) {
        this.status = status;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public Optional<LongFilter> optionalEventId() {
        return Optional.ofNullable(eventId);
    }

    public LongFilter eventId() {
        if (eventId == null) {
            setEventId(new LongFilter());
        }
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TrxTournamentCriteria that = (TrxTournamentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(prizeAmount, that.prizeAmount) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(location, that.location) &&
            Objects.equals(maxParticipants, that.maxParticipants) &&
            Objects.equals(status, that.status) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, prizeAmount, startDate, endDate, location, maxParticipants, status, eventId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrxTournamentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalPrizeAmount().map(f -> "prizeAmount=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalLocation().map(f -> "location=" + f + ", ").orElse("") +
            optionalMaxParticipants().map(f -> "maxParticipants=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalEventId().map(f -> "eventId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
