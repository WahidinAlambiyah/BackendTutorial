package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstSupplier;
import com.mycompany.myapp.domain.TrxOrderStock;
import com.mycompany.myapp.service.dto.MstSupplierDTO;
import com.mycompany.myapp.service.dto.TrxOrderStockDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxOrderStock} and its DTO {@link TrxOrderStockDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxOrderStockMapper extends EntityMapper<TrxOrderStockDTO, TrxOrderStock> {
    @Mapping(target = "supplier", source = "supplier", qualifiedByName = "mstSupplierId")
    TrxOrderStockDTO toDto(TrxOrderStock s);

    @Named("mstSupplierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstSupplierDTO toDtoMstSupplierId(MstSupplier mstSupplier);
}
