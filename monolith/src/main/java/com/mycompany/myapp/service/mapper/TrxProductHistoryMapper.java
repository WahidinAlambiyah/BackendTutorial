package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TrxProductHistory;
import com.mycompany.myapp.service.dto.TrxProductHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxProductHistory} and its DTO {@link TrxProductHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxProductHistoryMapper extends EntityMapper<TrxProductHistoryDTO, TrxProductHistory> {}
