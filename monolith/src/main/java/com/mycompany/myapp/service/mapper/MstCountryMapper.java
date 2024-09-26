package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCountry;
import com.mycompany.myapp.domain.MstRegion;
import com.mycompany.myapp.service.dto.MstCountryDTO;
import com.mycompany.myapp.service.dto.MstRegionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstCountry} and its DTO {@link MstCountryDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstCountryMapper extends EntityMapper<MstCountryDTO, MstCountry> {
    @Mapping(target = "region", source = "region", qualifiedByName = "mstRegionName")
    MstCountryDTO toDto(MstCountry s);

    @Named("mstRegionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MstRegionDTO toDtoMstRegionName(MstRegion mstRegion);
}
