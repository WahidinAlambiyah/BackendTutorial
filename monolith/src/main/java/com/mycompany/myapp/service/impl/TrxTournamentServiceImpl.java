package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxTournamentCriteria;
import com.mycompany.myapp.repository.TrxTournamentRepository;
import com.mycompany.myapp.repository.search.TrxTournamentSearchRepository;
import com.mycompany.myapp.service.TrxTournamentService;
import com.mycompany.myapp.service.dto.TrxTournamentDTO;
import com.mycompany.myapp.service.mapper.TrxTournamentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxTournament}.
 */
@Service
@Transactional
public class TrxTournamentServiceImpl implements TrxTournamentService {

    private static final Logger log = LoggerFactory.getLogger(TrxTournamentServiceImpl.class);

    private final TrxTournamentRepository trxTournamentRepository;

    private final TrxTournamentMapper trxTournamentMapper;

    private final TrxTournamentSearchRepository trxTournamentSearchRepository;

    public TrxTournamentServiceImpl(
        TrxTournamentRepository trxTournamentRepository,
        TrxTournamentMapper trxTournamentMapper,
        TrxTournamentSearchRepository trxTournamentSearchRepository
    ) {
        this.trxTournamentRepository = trxTournamentRepository;
        this.trxTournamentMapper = trxTournamentMapper;
        this.trxTournamentSearchRepository = trxTournamentSearchRepository;
    }

    @Override
    public Mono<TrxTournamentDTO> save(TrxTournamentDTO trxTournamentDTO) {
        log.debug("Request to save TrxTournament : {}", trxTournamentDTO);
        return trxTournamentRepository
            .save(trxTournamentMapper.toEntity(trxTournamentDTO))
            .flatMap(trxTournamentSearchRepository::save)
            .map(trxTournamentMapper::toDto);
    }

    @Override
    public Mono<TrxTournamentDTO> update(TrxTournamentDTO trxTournamentDTO) {
        log.debug("Request to update TrxTournament : {}", trxTournamentDTO);
        return trxTournamentRepository
            .save(trxTournamentMapper.toEntity(trxTournamentDTO))
            .flatMap(trxTournamentSearchRepository::save)
            .map(trxTournamentMapper::toDto);
    }

    @Override
    public Mono<TrxTournamentDTO> partialUpdate(TrxTournamentDTO trxTournamentDTO) {
        log.debug("Request to partially update TrxTournament : {}", trxTournamentDTO);

        return trxTournamentRepository
            .findById(trxTournamentDTO.getId())
            .map(existingTrxTournament -> {
                trxTournamentMapper.partialUpdate(existingTrxTournament, trxTournamentDTO);

                return existingTrxTournament;
            })
            .flatMap(trxTournamentRepository::save)
            .flatMap(savedTrxTournament -> {
                trxTournamentSearchRepository.save(savedTrxTournament);
                return Mono.just(savedTrxTournament);
            })
            .map(trxTournamentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxTournamentDTO> findByCriteria(TrxTournamentCriteria criteria) {
        log.debug("Request to get all TrxTournaments by Criteria");
        return trxTournamentRepository.findByCriteria(criteria, null).map(trxTournamentMapper::toDto);
    }

    /**
     * Find the count of trxTournaments by criteria.
     * @param criteria filtering criteria
     * @return the count of trxTournaments
     */
    public Mono<Long> countByCriteria(TrxTournamentCriteria criteria) {
        log.debug("Request to get the count of all TrxTournaments by Criteria");
        return trxTournamentRepository.countByCriteria(criteria);
    }

    public Flux<TrxTournamentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return trxTournamentRepository.findAllWithEagerRelationships(pageable).map(trxTournamentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return trxTournamentRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxTournamentSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxTournamentDTO> findOne(Long id) {
        log.debug("Request to get TrxTournament : {}", id);
        return trxTournamentRepository.findOneWithEagerRelationships(id).map(trxTournamentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxTournament : {}", id);
        return trxTournamentRepository.deleteById(id).then(trxTournamentSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxTournamentDTO> search(String query) {
        log.debug("Request to search TrxTournaments for query {}", query);
        try {
            return trxTournamentSearchRepository.search(query).map(trxTournamentMapper::toDto);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
