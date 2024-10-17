package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.TrxCart;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
import com.mycompany.myapp.service.dto.TrxCartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxCart} and its DTO {@link TrxCartDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxCartMapper extends EntityMapper<TrxCartDTO, TrxCart> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "mstCustomerId")
    TrxCartDTO toDto(TrxCart s);

    @Named("mstCustomerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstCustomerDTO toDtoMstCustomerId(MstCustomer mstCustomer);
}
