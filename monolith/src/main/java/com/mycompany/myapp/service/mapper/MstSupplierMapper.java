package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstSupplier;
import com.mycompany.myapp.service.dto.MstSupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstSupplier} and its DTO {@link MstSupplierDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstSupplierMapper extends EntityMapper<MstSupplierDTO, MstSupplier> {}
