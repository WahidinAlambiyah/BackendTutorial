package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.JobHistory;
import com.mycompany.myapp.domain.MstDepartment;
import com.mycompany.myapp.domain.MstEmployee;
import com.mycompany.myapp.domain.MstJob;
import com.mycompany.myapp.service.dto.JobHistoryDTO;
import com.mycompany.myapp.service.dto.MstDepartmentDTO;
import com.mycompany.myapp.service.dto.MstEmployeeDTO;
import com.mycompany.myapp.service.dto.MstJobDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link JobHistory} and its DTO {@link JobHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface JobHistoryMapper extends EntityMapper<JobHistoryDTO, JobHistory> {
    @Mapping(target = "job", source = "job", qualifiedByName = "mstJobId")
    @Mapping(target = "department", source = "department", qualifiedByName = "mstDepartmentId")
    @Mapping(target = "employee", source = "employee", qualifiedByName = "mstEmployeeId")
    JobHistoryDTO toDto(JobHistory s);

    @Named("mstJobId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstJobDTO toDtoMstJobId(MstJob mstJob);

    @Named("mstDepartmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstDepartmentDTO toDtoMstDepartmentId(MstDepartment mstDepartment);

    @Named("mstEmployeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstEmployeeDTO toDtoMstEmployeeId(MstEmployee mstEmployee);
}
