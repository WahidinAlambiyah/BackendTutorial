package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TrxStockAlert;
import com.mycompany.myapp.service.dto.TrxStockAlertDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxStockAlert} and its DTO {@link TrxStockAlertDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxStockAlertMapper extends EntityMapper<TrxStockAlertDTO, TrxStockAlert> {}
