package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxEventCriteria;
import com.mycompany.myapp.repository.TrxEventRepository;
import com.mycompany.myapp.repository.search.TrxEventSearchRepository;
import com.mycompany.myapp.service.TrxEventService;
import com.mycompany.myapp.service.dto.TrxEventDTO;
import com.mycompany.myapp.service.mapper.TrxEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxEvent}.
 */
@Service
@Transactional
public class TrxEventServiceImpl implements TrxEventService {

    private static final Logger log = LoggerFactory.getLogger(TrxEventServiceImpl.class);

    private final TrxEventRepository trxEventRepository;

    private final TrxEventMapper trxEventMapper;

    private final TrxEventSearchRepository trxEventSearchRepository;

    public TrxEventServiceImpl(
        TrxEventRepository trxEventRepository,
        TrxEventMapper trxEventMapper,
        TrxEventSearchRepository trxEventSearchRepository
    ) {
        this.trxEventRepository = trxEventRepository;
        this.trxEventMapper = trxEventMapper;
        this.trxEventSearchRepository = trxEventSearchRepository;
    }

    @Override
    public Mono<TrxEventDTO> save(TrxEventDTO trxEventDTO) {
        log.debug("Request to save TrxEvent : {}", trxEventDTO);
        return trxEventRepository
            .save(trxEventMapper.toEntity(trxEventDTO))
            .flatMap(trxEventSearchRepository::save)
            .map(trxEventMapper::toDto);
    }

    @Override
    public Mono<TrxEventDTO> update(TrxEventDTO trxEventDTO) {
        log.debug("Request to update TrxEvent : {}", trxEventDTO);
        return trxEventRepository
            .save(trxEventMapper.toEntity(trxEventDTO))
            .flatMap(trxEventSearchRepository::save)
            .map(trxEventMapper::toDto);
    }

    @Override
    public Mono<TrxEventDTO> partialUpdate(TrxEventDTO trxEventDTO) {
        log.debug("Request to partially update TrxEvent : {}", trxEventDTO);

        return trxEventRepository
            .findById(trxEventDTO.getId())
            .map(existingTrxEvent -> {
                trxEventMapper.partialUpdate(existingTrxEvent, trxEventDTO);

                return existingTrxEvent;
            })
            .flatMap(trxEventRepository::save)
            .flatMap(savedTrxEvent -> {
                trxEventSearchRepository.save(savedTrxEvent);
                return Mono.just(savedTrxEvent);
            })
            .map(trxEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxEventDTO> findByCriteria(TrxEventCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxEvents by Criteria");
        return trxEventRepository.findByCriteria(criteria, pageable).map(trxEventMapper::toDto);
    }

    /**
     * Find the count of trxEvents by criteria.
     * @param criteria filtering criteria
     * @return the count of trxEvents
     */
    public Mono<Long> countByCriteria(TrxEventCriteria criteria) {
        log.debug("Request to get the count of all TrxEvents by Criteria");
        return trxEventRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxEventRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxEventSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxEventDTO> findOne(Long id) {
        log.debug("Request to get TrxEvent : {}", id);
        return trxEventRepository.findById(id).map(trxEventMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxEvent : {}", id);
        return trxEventRepository.deleteById(id).then(trxEventSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxEventDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxEvents for query {}", query);
        return trxEventSearchRepository.search(query, pageable).map(trxEventMapper::toDto);
    }
}
