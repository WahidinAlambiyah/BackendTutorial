package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TrxEvent;
import com.mycompany.myapp.domain.TrxTournament;
import com.mycompany.myapp.service.dto.TrxEventDTO;
import com.mycompany.myapp.service.dto.TrxTournamentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrxTournament} and its DTO {@link TrxTournamentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrxTournamentMapper extends EntityMapper<TrxTournamentDTO, TrxTournament> {
    @Mapping(target = "event", source = "event", qualifiedByName = "trxEventTitle")
    TrxTournamentDTO toDto(TrxTournament s);

    @Named("trxEventTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    TrxEventDTO toDtoTrxEventTitle(TrxEvent trxEvent);
}
