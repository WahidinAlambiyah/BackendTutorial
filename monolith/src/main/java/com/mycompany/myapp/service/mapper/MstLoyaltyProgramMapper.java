package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.MstLoyaltyProgram;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
import com.mycompany.myapp.service.dto.MstLoyaltyProgramDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MstLoyaltyProgram} and its DTO {@link MstLoyaltyProgramDTO}.
 */
@Mapper(componentModel = "spring")
public interface MstLoyaltyProgramMapper extends EntityMapper<MstLoyaltyProgramDTO, MstLoyaltyProgram> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "mstCustomerId")
    MstLoyaltyProgramDTO toDto(MstLoyaltyProgram s);

    @Named("mstCustomerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstCustomerDTO toDtoMstCustomerId(MstCustomer mstCustomer);
}
