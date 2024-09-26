package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstDistrict;
import com.mycompany.myapp.domain.MstSubDistrict;
import com.mycompany.myapp.service.dto.MstDistrictDTO;
import com.mycompany.myapp.service.dto.MstSubDistrictDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstSubDistrict} and its DTO {@link MstSubDistrictDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstSubDistrictMapper extends EntityMapper<MstSubDistrictDTO, MstSubDistrict> {
    @Mapping(target = "district", source = "district", qualifiedByName = "mstDistrictName")
    MstSubDistrictDTO toDto(MstSubDistrict s);

    @Named("mstDistrictName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MstDistrictDTO toDtoMstDistrictName(MstDistrict mstDistrict);
}
