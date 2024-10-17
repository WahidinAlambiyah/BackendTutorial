package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstCustomer} and its DTO {@link MstCustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstCustomerMapper extends EntityMapper<MstCustomerDTO, MstCustomer> {}
