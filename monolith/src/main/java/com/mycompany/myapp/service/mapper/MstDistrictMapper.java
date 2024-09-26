package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCity;
import com.mycompany.myapp.domain.MstDistrict;
import com.mycompany.myapp.service.dto.MstCityDTO;
import com.mycompany.myapp.service.dto.MstDistrictDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstDistrict} and its DTO {@link MstDistrictDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstDistrictMapper extends EntityMapper<MstDistrictDTO, MstDistrict> {
    @Mapping(target = "city", source = "city", qualifiedByName = "mstCityName")
    MstDistrictDTO toDto(MstDistrict s);

    @Named("mstCityName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MstCityDTO toDtoMstCityName(MstCity mstCity);
}
