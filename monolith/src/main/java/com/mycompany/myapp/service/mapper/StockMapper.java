package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.MstProduct;
import com.mycompany.myapp.domain.Stock;
import com.mycompany.myapp.service.dto.MstProductDTO;
import com.mycompany.myapp.service.dto.StockDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Stock} and its DTO {@link StockDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockMapper extends EntityMapper<StockDTO, Stock> {
    @Mapping(target = "product", source = "product", qualifiedByName = "mstProductId")
    StockDTO toDto(Stock s);

    @Named("mstProductId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MstProductDTO toDtoMstProductId(MstProduct mstProduct);
}
