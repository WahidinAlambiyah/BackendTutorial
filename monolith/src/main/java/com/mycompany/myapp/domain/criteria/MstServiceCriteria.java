package com.mycompany.myapp.domain.criteria;

import com.mycompany.myapp.domain.enumeration.ServiceType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.MstService} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.MstServiceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /mst-services?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstServiceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ServiceType
     */
    public static class ServiceTypeFilter extends Filter<ServiceType> {

        public ServiceTypeFilter() {}

        public ServiceTypeFilter(ServiceTypeFilter filter) {
            super(filter);
        }

        @Override
        public ServiceTypeFilter copy() {
            return new ServiceTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BigDecimalFilter price;

    private IntegerFilter durationInHours;

    private ServiceTypeFilter serviceType;

    private LongFilter testimonialId;

    private Boolean distinct;

    public MstServiceCriteria() {}

    public MstServiceCriteria(MstServiceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.durationInHours = other.optionalDurationInHours().map(IntegerFilter::copy).orElse(null);
        this.serviceType = other.optionalServiceType().map(ServiceTypeFilter::copy).orElse(null);
        this.testimonialId = other.optionalTestimonialId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MstServiceCriteria copy() {
        return new MstServiceCriteria(this);
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

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public IntegerFilter getDurationInHours() {
        return durationInHours;
    }

    public Optional<IntegerFilter> optionalDurationInHours() {
        return Optional.ofNullable(durationInHours);
    }

    public IntegerFilter durationInHours() {
        if (durationInHours == null) {
            setDurationInHours(new IntegerFilter());
        }
        return durationInHours;
    }

    public void setDurationInHours(IntegerFilter durationInHours) {
        this.durationInHours = durationInHours;
    }

    public ServiceTypeFilter getServiceType() {
        return serviceType;
    }

    public Optional<ServiceTypeFilter> optionalServiceType() {
        return Optional.ofNullable(serviceType);
    }

    public ServiceTypeFilter serviceType() {
        if (serviceType == null) {
            setServiceType(new ServiceTypeFilter());
        }
        return serviceType;
    }

    public void setServiceType(ServiceTypeFilter serviceType) {
        this.serviceType = serviceType;
    }

    public LongFilter getTestimonialId() {
        return testimonialId;
    }

    public Optional<LongFilter> optionalTestimonialId() {
        return Optional.ofNullable(testimonialId);
    }

    public LongFilter testimonialId() {
        if (testimonialId == null) {
            setTestimonialId(new LongFilter());
        }
        return testimonialId;
    }

    public void setTestimonialId(LongFilter testimonialId) {
        this.testimonialId = testimonialId;
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
        final MstServiceCriteria that = (MstServiceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(price, that.price) &&
            Objects.equals(durationInHours, that.durationInHours) &&
            Objects.equals(serviceType, that.serviceType) &&
            Objects.equals(testimonialId, that.testimonialId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, durationInHours, serviceType, testimonialId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstServiceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalDurationInHours().map(f -> "durationInHours=" + f + ", ").orElse("") +
            optionalServiceType().map(f -> "serviceType=" + f + ", ").orElse("") +
            optionalTestimonialId().map(f -> "testimonialId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
