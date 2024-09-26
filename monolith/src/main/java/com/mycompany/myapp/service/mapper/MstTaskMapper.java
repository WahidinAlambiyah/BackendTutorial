package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstJob;
import com.mycompany.myapp.domain.MstTask;
import com.mycompany.myapp.service.dto.MstJobDTO;
import com.mycompany.myapp.service.dto.MstTaskDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstTask} and its DTO {@link MstTaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstTaskMapper extends EntityMapper<MstTaskDTO, MstTask> {
    @Mapping(target = "jobs", source = "jobs", qualifiedByName = "mstJobIdSet")
    MstTaskDTO toDto(MstTask s);

    @Mapping(target = "jobs", ignore = true)
    @Mapping(target = "removeJob", ignore = true)
    MstTask toEntity(MstTaskDTO mstTaskDTO);

    @Named("mstJobId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstJobDTO toDtoMstJobId(MstJob mstJob);

    @Named("mstJobIdSet")
    default Set<MstJobDTO> toDtoMstJobIdSet(Set<MstJob> mstJob) {
        return mstJob.stream().map(this::toDtoMstJobId).collect(Collectors.toSet());
    }
}
