package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.City;
import com.mycompany.myapp.domain.Province;
import com.mycompany.myapp.service.dto.CityDTO;
import com.mycompany.myapp.service.dto.ProvinceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link City} and its DTO {@link CityDTO}.
 */
@Mapper(componentModel = "spring")
public interface CityMapper extends EntityMapper<CityDTO, City> {
    @Mapping(target = "province", source = "province", qualifiedByName = "provinceName")
    CityDTO toDto(City s);

    @Named("provinceName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProvinceDTO toDtoProvinceName(Province province);
}
