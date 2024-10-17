package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxProductHistoryCriteria;
import com.mycompany.myapp.repository.TrxProductHistoryRepository;
import com.mycompany.myapp.repository.search.TrxProductHistorySearchRepository;
import com.mycompany.myapp.service.TrxProductHistoryService;
import com.mycompany.myapp.service.dto.TrxProductHistoryDTO;
import com.mycompany.myapp.service.mapper.TrxProductHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxProductHistory}.
 */
@Service
@Transactional
public class TrxProductHistoryServiceImpl implements TrxProductHistoryService {

    private static final Logger log = LoggerFactory.getLogger(TrxProductHistoryServiceImpl.class);

    private final TrxProductHistoryRepository trxProductHistoryRepository;

    private final TrxProductHistoryMapper trxProductHistoryMapper;

    private final TrxProductHistorySearchRepository trxProductHistorySearchRepository;

    public TrxProductHistoryServiceImpl(
        TrxProductHistoryRepository trxProductHistoryRepository,
        TrxProductHistoryMapper trxProductHistoryMapper,
        TrxProductHistorySearchRepository trxProductHistorySearchRepository
    ) {
        this.trxProductHistoryRepository = trxProductHistoryRepository;
        this.trxProductHistoryMapper = trxProductHistoryMapper;
        this.trxProductHistorySearchRepository = trxProductHistorySearchRepository;
    }

    @Override
    public Mono<TrxProductHistoryDTO> save(TrxProductHistoryDTO trxProductHistoryDTO) {
        log.debug("Request to save TrxProductHistory : {}", trxProductHistoryDTO);
        return trxProductHistoryRepository
            .save(trxProductHistoryMapper.toEntity(trxProductHistoryDTO))
            .flatMap(trxProductHistorySearchRepository::save)
            .map(trxProductHistoryMapper::toDto);
    }

    @Override
    public Mono<TrxProductHistoryDTO> update(TrxProductHistoryDTO trxProductHistoryDTO) {
        log.debug("Request to update TrxProductHistory : {}", trxProductHistoryDTO);
        return trxProductHistoryRepository
            .save(trxProductHistoryMapper.toEntity(trxProductHistoryDTO))
            .flatMap(trxProductHistorySearchRepository::save)
            .map(trxProductHistoryMapper::toDto);
    }

    @Override
    public Mono<TrxProductHistoryDTO> partialUpdate(TrxProductHistoryDTO trxProductHistoryDTO) {
        log.debug("Request to partially update TrxProductHistory : {}", trxProductHistoryDTO);

        return trxProductHistoryRepository
            .findById(trxProductHistoryDTO.getId())
            .map(existingTrxProductHistory -> {
                trxProductHistoryMapper.partialUpdate(existingTrxProductHistory, trxProductHistoryDTO);

                return existingTrxProductHistory;
            })
            .flatMap(trxProductHistoryRepository::save)
            .flatMap(savedTrxProductHistory -> {
                trxProductHistorySearchRepository.save(savedTrxProductHistory);
                return Mono.just(savedTrxProductHistory);
            })
            .map(trxProductHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxProductHistoryDTO> findByCriteria(TrxProductHistoryCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxProductHistories by Criteria");
        return trxProductHistoryRepository.findByCriteria(criteria, pageable).map(trxProductHistoryMapper::toDto);
    }

    /**
     * Find the count of trxProductHistories by criteria.
     * @param criteria filtering criteria
     * @return the count of trxProductHistories
     */
    public Mono<Long> countByCriteria(TrxProductHistoryCriteria criteria) {
        log.debug("Request to get the count of all TrxProductHistories by Criteria");
        return trxProductHistoryRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxProductHistoryRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxProductHistorySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxProductHistoryDTO> findOne(Long id) {
        log.debug("Request to get TrxProductHistory : {}", id);
        return trxProductHistoryRepository.findById(id).map(trxProductHistoryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxProductHistory : {}", id);
        return trxProductHistoryRepository.deleteById(id).then(trxProductHistorySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxProductHistoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxProductHistories for query {}", query);
        return trxProductHistorySearchRepository.search(query, pageable).map(trxProductHistoryMapper::toDto);
    }
}
