package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Location;
import com.mycompany.myapp.domain.MstDepartment;
import com.mycompany.myapp.service.dto.LocationDTO;
import com.mycompany.myapp.service.dto.MstDepartmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstDepartment} and its DTO {@link MstDepartmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstDepartmentMapper extends EntityMapper<MstDepartmentDTO, MstDepartment> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationId")
    MstDepartmentDTO toDto(MstDepartment s);

    @Named("locationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LocationDTO toDtoLocationId(Location location);
}
