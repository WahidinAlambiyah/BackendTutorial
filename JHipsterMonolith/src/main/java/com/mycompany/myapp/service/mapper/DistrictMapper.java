package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.City;
import com.mycompany.myapp.domain.District;
import com.mycompany.myapp.service.dto.CityDTO;
import com.mycompany.myapp.service.dto.DistrictDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link District} and its DTO {@link DistrictDTO}.
 */
@Mapper(componentModel = "spring")
public interface DistrictMapper extends EntityMapper<DistrictDTO, District> {
    @Mapping(target = "city", source = "city", qualifiedByName = "cityName")
    DistrictDTO toDto(District s);

    @Named("cityName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CityDTO toDtoCityName(City city);
}
