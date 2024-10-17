package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
import com.mycompany.myapp.service.dto.TrxOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxOrder} and its DTO {@link TrxOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxOrderMapper extends EntityMapper<TrxOrderDTO, TrxOrder> {
    @Mapping(target = "mstCustomer", source = "mstCustomer", qualifiedByName = "mstCustomerId")
    TrxOrderDTO toDto(TrxOrder s);

    @Named("mstCustomerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstCustomerDTO toDtoMstCustomerId(MstCustomer mstCustomer);
}
