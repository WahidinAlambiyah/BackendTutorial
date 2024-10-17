package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstProduct;
import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.domain.TrxOrderItem;
import com.mycompany.myapp.service.dto.MstProductDTO;
import com.mycompany.myapp.service.dto.TrxOrderDTO;
import com.mycompany.myapp.service.dto.TrxOrderItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxOrderItem} and its DTO {@link TrxOrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxOrderItemMapper extends EntityMapper<TrxOrderItemDTO, TrxOrderItem> {
    @Mapping(target = "order", source = "order", qualifiedByName = "trxOrderId")
    @Mapping(target = "product", source = "product", qualifiedByName = "mstProductId")
    TrxOrderItemDTO toDto(TrxOrderItem s);

    @Named("trxOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TrxOrderDTO toDtoTrxOrderId(TrxOrder trxOrder);

    @Named("mstProductId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstProductDTO toDtoMstProductId(MstProduct mstProduct);
}
