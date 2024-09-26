package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstEmployee;
import com.mycompany.myapp.domain.MstJob;
import com.mycompany.myapp.domain.MstTask;
import com.mycompany.myapp.service.dto.MstEmployeeDTO;
import com.mycompany.myapp.service.dto.MstJobDTO;
import com.mycompany.myapp.service.dto.MstTaskDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstJob} and its DTO {@link MstJobDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstJobMapper extends EntityMapper<MstJobDTO, MstJob> {
    @Mapping(target = "tasks", source = "tasks", qualifiedByName = "mstTaskTitleSet")
    @Mapping(target = "employee", source = "employee", qualifiedByName = "mstEmployeeId")
    MstJobDTO toDto(MstJob s);

    @Mapping(target = "removeTask", ignore = true)
    MstJob toEntity(MstJobDTO mstJobDTO);

    @Named("mstTaskTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    MstTaskDTO toDtoMstTaskTitle(MstTask mstTask);

    @Named("mstTaskTitleSet")
    default Set<MstTaskDTO> toDtoMstTaskTitleSet(Set<MstTask> mstTask) {
        return mstTask.stream().map(this::toDtoMstTaskTitle).collect(Collectors.toSet());
    }

    @Named("mstEmployeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstEmployeeDTO toDtoMstEmployeeId(MstEmployee mstEmployee);
}
