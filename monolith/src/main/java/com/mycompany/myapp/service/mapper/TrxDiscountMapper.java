package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TrxDiscount;
import com.mycompany.myapp.service.dto.TrxDiscountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxDiscount} and its DTO {@link TrxDiscountDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxDiscountMapper extends EntityMapper<TrxDiscountDTO, TrxDiscount> {}
