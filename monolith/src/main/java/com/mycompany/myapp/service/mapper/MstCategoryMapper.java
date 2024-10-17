package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCategory;
import com.mycompany.myapp.service.dto.MstCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstCategory} and its DTO {@link MstCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstCategoryMapper extends EntityMapper<MstCategoryDTO, MstCategory> {}
