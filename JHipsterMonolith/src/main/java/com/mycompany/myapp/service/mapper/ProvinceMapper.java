package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Country;
import com.mycompany.myapp.domain.Province;
import com.mycompany.myapp.service.dto.CountryDTO;
import com.mycompany.myapp.service.dto.ProvinceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Province} and its DTO {@link ProvinceDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProvinceMapper extends EntityMapper<ProvinceDTO, Province> {
    @Mapping(target = "country", source = "country", qualifiedByName = "countryName")
    ProvinceDTO toDto(Province s);

    @Named("countryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CountryDTO toDtoCountryName(Country country);
}
