package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PostalCode;
import com.mycompany.myapp.domain.SubDistrict;
import com.mycompany.myapp.service.dto.PostalCodeDTO;
import com.mycompany.myapp.service.dto.SubDistrictDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PostalCode} and its DTO {@link PostalCodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostalCodeMapper extends EntityMapper<PostalCodeDTO, PostalCode> {
    @Mapping(target = "subDistrict", source = "subDistrict", qualifiedByName = "subDistrictName")
    PostalCodeDTO toDto(PostalCode s);

    @Named("subDistrictName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SubDistrictDTO toDtoSubDistrictName(SubDistrict subDistrict);
}
