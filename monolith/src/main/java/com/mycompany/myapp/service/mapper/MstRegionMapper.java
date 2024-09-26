package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstRegion;
import com.mycompany.myapp.service.dto.MstRegionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstRegion} and its DTO {@link MstRegionDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstRegionMapper extends EntityMapper<MstRegionDTO, MstRegion> {}
