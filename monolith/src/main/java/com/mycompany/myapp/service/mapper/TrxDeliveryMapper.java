package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstDriver;
import com.mycompany.myapp.domain.TrxDelivery;
import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.service.dto.MstDriverDTO;
import com.mycompany.myapp.service.dto.TrxDeliveryDTO;
import com.mycompany.myapp.service.dto.TrxOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxDelivery} and its DTO {@link TrxDeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxDeliveryMapper extends EntityMapper<TrxDeliveryDTO, TrxDelivery> {
    @Mapping(target = "driver", source = "driver", qualifiedByName = "mstDriverId")
    @Mapping(target = "trxOrder", source = "trxOrder", qualifiedByName = "trxOrderId")
    TrxDeliveryDTO toDto(TrxDelivery s);

    @Named("mstDriverId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstDriverDTO toDtoMstDriverId(MstDriver mstDriver);

    @Named("trxOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TrxOrderDTO toDtoTrxOrderId(TrxOrder trxOrder);
}
