package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxOrderHistoryCriteria;
import com.mycompany.myapp.repository.TrxOrderHistoryRepository;
import com.mycompany.myapp.repository.search.TrxOrderHistorySearchRepository;
import com.mycompany.myapp.service.TrxOrderHistoryService;
import com.mycompany.myapp.service.dto.TrxOrderHistoryDTO;
import com.mycompany.myapp.service.mapper.TrxOrderHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxOrderHistory}.
 */
@Service
@Transactional
public class TrxOrderHistoryServiceImpl implements TrxOrderHistoryService {

    private static final Logger log = LoggerFactory.getLogger(TrxOrderHistoryServiceImpl.class);

    private final TrxOrderHistoryRepository trxOrderHistoryRepository;

    private final TrxOrderHistoryMapper trxOrderHistoryMapper;

    private final TrxOrderHistorySearchRepository trxOrderHistorySearchRepository;

    public TrxOrderHistoryServiceImpl(
        TrxOrderHistoryRepository trxOrderHistoryRepository,
        TrxOrderHistoryMapper trxOrderHistoryMapper,
        TrxOrderHistorySearchRepository trxOrderHistorySearchRepository
    ) {
        this.trxOrderHistoryRepository = trxOrderHistoryRepository;
        this.trxOrderHistoryMapper = trxOrderHistoryMapper;
        this.trxOrderHistorySearchRepository = trxOrderHistorySearchRepository;
    }

    @Override
    public Mono<TrxOrderHistoryDTO> save(TrxOrderHistoryDTO trxOrderHistoryDTO) {
        log.debug("Request to save TrxOrderHistory : {}", trxOrderHistoryDTO);
        return trxOrderHistoryRepository
            .save(trxOrderHistoryMapper.toEntity(trxOrderHistoryDTO))
            .flatMap(trxOrderHistorySearchRepository::save)
            .map(trxOrderHistoryMapper::toDto);
    }

    @Override
    public Mono<TrxOrderHistoryDTO> update(TrxOrderHistoryDTO trxOrderHistoryDTO) {
        log.debug("Request to update TrxOrderHistory : {}", trxOrderHistoryDTO);
        return trxOrderHistoryRepository
            .save(trxOrderHistoryMapper.toEntity(trxOrderHistoryDTO))
            .flatMap(trxOrderHistorySearchRepository::save)
            .map(trxOrderHistoryMapper::toDto);
    }

    @Override
    public Mono<TrxOrderHistoryDTO> partialUpdate(TrxOrderHistoryDTO trxOrderHistoryDTO) {
        log.debug("Request to partially update TrxOrderHistory : {}", trxOrderHistoryDTO);

        return trxOrderHistoryRepository
            .findById(trxOrderHistoryDTO.getId())
            .map(existingTrxOrderHistory -> {
                trxOrderHistoryMapper.partialUpdate(existingTrxOrderHistory, trxOrderHistoryDTO);

                return existingTrxOrderHistory;
            })
            .flatMap(trxOrderHistoryRepository::save)
            .flatMap(savedTrxOrderHistory -> {
                trxOrderHistorySearchRepository.save(savedTrxOrderHistory);
                return Mono.just(savedTrxOrderHistory);
            })
            .map(trxOrderHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxOrderHistoryDTO> findByCriteria(TrxOrderHistoryCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxOrderHistories by Criteria");
        return trxOrderHistoryRepository.findByCriteria(criteria, pageable).map(trxOrderHistoryMapper::toDto);
    }

    /**
     * Find the count of trxOrderHistories by criteria.
     * @param criteria filtering criteria
     * @return the count of trxOrderHistories
     */
    public Mono<Long> countByCriteria(TrxOrderHistoryCriteria criteria) {
        log.debug("Request to get the count of all TrxOrderHistories by Criteria");
        return trxOrderHistoryRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxOrderHistoryRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxOrderHistorySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxOrderHistoryDTO> findOne(Long id) {
        log.debug("Request to get TrxOrderHistory : {}", id);
        return trxOrderHistoryRepository.findById(id).map(trxOrderHistoryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxOrderHistory : {}", id);
        return trxOrderHistoryRepository.deleteById(id).then(trxOrderHistorySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxOrderHistoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxOrderHistories for query {}", query);
        return trxOrderHistorySearchRepository.search(query, pageable).map(trxOrderHistoryMapper::toDto);
    }
}
