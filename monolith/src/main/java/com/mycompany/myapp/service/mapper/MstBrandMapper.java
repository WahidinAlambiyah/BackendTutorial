package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstBrand;
import com.mycompany.myapp.service.dto.MstBrandDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstBrand} and its DTO {@link MstBrandDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstBrandMapper extends EntityMapper<MstBrandDTO, MstBrand> {}
