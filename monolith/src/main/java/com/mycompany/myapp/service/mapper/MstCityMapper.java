package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCity;
import com.mycompany.myapp.domain.MstProvince;
import com.mycompany.myapp.service.dto.MstCityDTO;
import com.mycompany.myapp.service.dto.MstProvinceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstCity} and its DTO {@link MstCityDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstCityMapper extends EntityMapper<MstCityDTO, MstCity> {
    @Mapping(target = "province", source = "province", qualifiedByName = "mstProvinceName")
    MstCityDTO toDto(MstCity s);

    @Named("mstProvinceName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MstProvinceDTO toDtoMstProvinceName(MstProvince mstProvince);
}
