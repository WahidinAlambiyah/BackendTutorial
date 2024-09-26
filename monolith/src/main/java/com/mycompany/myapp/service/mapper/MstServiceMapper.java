package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstService;
import com.mycompany.myapp.domain.TrxTestimonial;
import com.mycompany.myapp.service.dto.MstServiceDTO;
import com.mycompany.myapp.service.dto.TrxTestimonialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstService} and its DTO {@link MstServiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstServiceMapper extends EntityMapper<MstServiceDTO, MstService> {
    @Mapping(target = "testimonial", source = "testimonial", qualifiedByName = "trxTestimonialId")
    MstServiceDTO toDto(MstService s);

    @Named("trxTestimonialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TrxTestimonialDTO toDtoTrxTestimonialId(TrxTestimonial trxTestimonial);
}
