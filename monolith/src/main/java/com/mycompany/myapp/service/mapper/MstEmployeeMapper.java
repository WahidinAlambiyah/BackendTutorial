package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstDepartment;
import com.mycompany.myapp.domain.MstEmployee;
import com.mycompany.myapp.service.dto.MstDepartmentDTO;
import com.mycompany.myapp.service.dto.MstEmployeeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstEmployee} and its DTO {@link MstEmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstEmployeeMapper extends EntityMapper<MstEmployeeDTO, MstEmployee> {
    @Mapping(target = "manager", source = "manager", qualifiedByName = "mstEmployeeId")
    @Mapping(target = "department", source = "department", qualifiedByName = "mstDepartmentId")
    @Mapping(target = "mstDepartment", source = "mstDepartment", qualifiedByName = "mstDepartmentId")
    MstEmployeeDTO toDto(MstEmployee s);

    @Named("mstEmployeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstEmployeeDTO toDtoMstEmployeeId(MstEmployee mstEmployee);

    @Named("mstDepartmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstDepartmentDTO toDtoMstDepartmentId(MstDepartment mstDepartment);
}
