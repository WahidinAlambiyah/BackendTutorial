package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TrxOrderHistory;
import com.mycompany.myapp.service.dto.TrxOrderHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxOrderHistory} and its DTO {@link TrxOrderHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxOrderHistoryMapper extends EntityMapper<TrxOrderHistoryDTO, TrxOrderHistory> {}
