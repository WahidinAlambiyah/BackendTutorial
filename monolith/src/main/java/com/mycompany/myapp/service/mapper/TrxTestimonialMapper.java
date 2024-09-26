package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TrxTestimonial;
import com.mycompany.myapp.service.dto.TrxTestimonialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxTestimonial} and its DTO {@link TrxTestimonialDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxTestimonialMapper extends EntityMapper<TrxTestimonialDTO, TrxTestimonial> {}
