package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCountry;
import com.mycompany.myapp.domain.MstProvince;
import com.mycompany.myapp.service.dto.MstCountryDTO;
import com.mycompany.myapp.service.dto.MstProvinceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstProvince} and its DTO {@link MstProvinceDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstProvinceMapper extends EntityMapper<MstProvinceDTO, MstProvince> {
    @Mapping(target = "country", source = "country", qualifiedByName = "mstCountryName")
    MstProvinceDTO toDto(MstProvince s);

    @Named("mstCountryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MstCountryDTO toDtoMstCountryName(MstCountry mstCountry);
}
