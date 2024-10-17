package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstDriver;
import com.mycompany.myapp.service.dto.MstDriverDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstDriver} and its DTO {@link MstDriverDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstDriverMapper extends EntityMapper<MstDriverDTO, MstDriver> {}
