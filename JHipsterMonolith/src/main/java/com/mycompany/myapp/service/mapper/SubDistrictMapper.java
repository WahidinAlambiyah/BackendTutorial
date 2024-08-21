package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.District;
import com.mycompany.myapp.domain.SubDistrict;
import com.mycompany.myapp.service.dto.DistrictDTO;
import com.mycompany.myapp.service.dto.SubDistrictDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SubDistrict} and its DTO {@link SubDistrictDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubDistrictMapper extends EntityMapper<SubDistrictDTO, SubDistrict> {
    @Mapping(target = "district", source = "district", qualifiedByName = "districtName")
    SubDistrictDTO toDto(SubDistrict s);

    @Named("districtName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DistrictDTO toDtoDistrictName(District district);
}
