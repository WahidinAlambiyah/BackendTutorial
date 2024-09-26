package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstPostalCode;
import com.mycompany.myapp.domain.MstSubDistrict;
import com.mycompany.myapp.service.dto.MstPostalCodeDTO;
import com.mycompany.myapp.service.dto.MstSubDistrictDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstPostalCode} and its DTO {@link MstPostalCodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstPostalCodeMapper extends EntityMapper<MstPostalCodeDTO, MstPostalCode> {
    @Mapping(target = "subDistrict", source = "subDistrict", qualifiedByName = "mstSubDistrictName")
    MstPostalCodeDTO toDto(MstPostalCode s);

    @Named("mstSubDistrictName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MstSubDistrictDTO toDtoMstSubDistrictName(MstSubDistrict mstSubDistrict);
}
