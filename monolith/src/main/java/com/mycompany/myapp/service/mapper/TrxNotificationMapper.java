package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.TrxNotification;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
import com.mycompany.myapp.service.dto.TrxNotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxNotification} and its DTO {@link TrxNotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxNotificationMapper extends EntityMapper<TrxNotificationDTO, TrxNotification> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "mstCustomerId")
    TrxNotificationDTO toDto(TrxNotification s);

    @Named("mstCustomerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstCustomerDTO toDtoMstCustomerId(MstCustomer mstCustomer);
}
