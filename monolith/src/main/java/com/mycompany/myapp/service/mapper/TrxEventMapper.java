package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstService;
import com.mycompany.myapp.domain.TrxEvent;
import com.mycompany.myapp.domain.TrxTestimonial;
import com.mycompany.myapp.service.dto.MstServiceDTO;
import com.mycompany.myapp.service.dto.TrxEventDTO;
import com.mycompany.myapp.service.dto.TrxTestimonialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxEvent} and its DTO {@link TrxEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxEventMapper extends EntityMapper<TrxEventDTO, TrxEvent> {
    @Mapping(target = "service", source = "service", qualifiedByName = "mstServiceId")
    @Mapping(target = "testimonial", source = "testimonial", qualifiedByName = "trxTestimonialId")
    TrxEventDTO toDto(TrxEvent s);

    @Named("mstServiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstServiceDTO toDtoMstServiceId(MstService mstService);

    @Named("trxTestimonialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TrxTestimonialDTO toDtoTrxTestimonialId(TrxTestimonial trxTestimonial);
}
