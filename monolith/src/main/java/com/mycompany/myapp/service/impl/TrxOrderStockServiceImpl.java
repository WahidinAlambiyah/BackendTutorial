package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxOrderStockCriteria;
import com.mycompany.myapp.repository.TrxOrderStockRepository;
import com.mycompany.myapp.repository.search.TrxOrderStockSearchRepository;
import com.mycompany.myapp.service.TrxOrderStockService;
import com.mycompany.myapp.service.dto.TrxOrderStockDTO;
import com.mycompany.myapp.service.mapper.TrxOrderStockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxOrderStock}.
 */
@Service
@Transactional
public class TrxOrderStockServiceImpl implements TrxOrderStockService {

    private static final Logger log = LoggerFactory.getLogger(TrxOrderStockServiceImpl.class);

    private final TrxOrderStockRepository trxOrderStockRepository;

    private final TrxOrderStockMapper trxOrderStockMapper;

    private final TrxOrderStockSearchRepository trxOrderStockSearchRepository;

    public TrxOrderStockServiceImpl(
        TrxOrderStockRepository trxOrderStockRepository,
        TrxOrderStockMapper trxOrderStockMapper,
        TrxOrderStockSearchRepository trxOrderStockSearchRepository
    ) {
        this.trxOrderStockRepository = trxOrderStockRepository;
        this.trxOrderStockMapper = trxOrderStockMapper;
        this.trxOrderStockSearchRepository = trxOrderStockSearchRepository;
    }

    @Override
    public Mono<TrxOrderStockDTO> save(TrxOrderStockDTO trxOrderStockDTO) {
        log.debug("Request to save TrxOrderStock : {}", trxOrderStockDTO);
        return trxOrderStockRepository
            .save(trxOrderStockMapper.toEntity(trxOrderStockDTO))
            .flatMap(trxOrderStockSearchRepository::save)
            .map(trxOrderStockMapper::toDto);
    }

    @Override
    public Mono<TrxOrderStockDTO> update(TrxOrderStockDTO trxOrderStockDTO) {
        log.debug("Request to update TrxOrderStock : {}", trxOrderStockDTO);
        return trxOrderStockRepository
            .save(trxOrderStockMapper.toEntity(trxOrderStockDTO))
            .flatMap(trxOrderStockSearchRepository::save)
            .map(trxOrderStockMapper::toDto);
    }

    @Override
    public Mono<TrxOrderStockDTO> partialUpdate(TrxOrderStockDTO trxOrderStockDTO) {
        log.debug("Request to partially update TrxOrderStock : {}", trxOrderStockDTO);

        return trxOrderStockRepository
            .findById(trxOrderStockDTO.getId())
            .map(existingTrxOrderStock -> {
                trxOrderStockMapper.partialUpdate(existingTrxOrderStock, trxOrderStockDTO);

                return existingTrxOrderStock;
            })
            .flatMap(trxOrderStockRepository::save)
            .flatMap(savedTrxOrderStock -> {
                trxOrderStockSearchRepository.save(savedTrxOrderStock);
                return Mono.just(savedTrxOrderStock);
            })
            .map(trxOrderStockMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxOrderStockDTO> findByCriteria(TrxOrderStockCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxOrderStocks by Criteria");
        return trxOrderStockRepository.findByCriteria(criteria, pageable).map(trxOrderStockMapper::toDto);
    }

    /**
     * Find the count of trxOrderStocks by criteria.
     * @param criteria filtering criteria
     * @return the count of trxOrderStocks
     */
    public Mono<Long> countByCriteria(TrxOrderStockCriteria criteria) {
        log.debug("Request to get the count of all TrxOrderStocks by Criteria");
        return trxOrderStockRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxOrderStockRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxOrderStockSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxOrderStockDTO> findOne(Long id) {
        log.debug("Request to get TrxOrderStock : {}", id);
        return trxOrderStockRepository.findById(id).map(trxOrderStockMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxOrderStock : {}", id);
        return trxOrderStockRepository.deleteById(id).then(trxOrderStockSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxOrderStockDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxOrderStocks for query {}", query);
        return trxOrderStockSearchRepository.search(query, pageable).map(trxOrderStockMapper::toDto);
    }
}
