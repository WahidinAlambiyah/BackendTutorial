package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxOrderCriteria;
import com.mycompany.myapp.repository.TrxOrderRepository;
import com.mycompany.myapp.repository.search.TrxOrderSearchRepository;
import com.mycompany.myapp.service.TrxOrderService;
import com.mycompany.myapp.service.dto.TrxOrderDTO;
import com.mycompany.myapp.service.mapper.TrxOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxOrder}.
 */
@Service
@Transactional
public class TrxOrderServiceImpl implements TrxOrderService {

    private static final Logger log = LoggerFactory.getLogger(TrxOrderServiceImpl.class);

    private final TrxOrderRepository trxOrderRepository;

    private final TrxOrderMapper trxOrderMapper;

    private final TrxOrderSearchRepository trxOrderSearchRepository;

    public TrxOrderServiceImpl(
        TrxOrderRepository trxOrderRepository,
        TrxOrderMapper trxOrderMapper,
        TrxOrderSearchRepository trxOrderSearchRepository
    ) {
        this.trxOrderRepository = trxOrderRepository;
        this.trxOrderMapper = trxOrderMapper;
        this.trxOrderSearchRepository = trxOrderSearchRepository;
    }

    @Override
    public Mono<TrxOrderDTO> save(TrxOrderDTO trxOrderDTO) {
        log.debug("Request to save TrxOrder : {}", trxOrderDTO);
        return trxOrderRepository
            .save(trxOrderMapper.toEntity(trxOrderDTO))
            .flatMap(trxOrderSearchRepository::save)
            .map(trxOrderMapper::toDto);
    }

    @Override
    public Mono<TrxOrderDTO> update(TrxOrderDTO trxOrderDTO) {
        log.debug("Request to update TrxOrder : {}", trxOrderDTO);
        return trxOrderRepository
            .save(trxOrderMapper.toEntity(trxOrderDTO))
            .flatMap(trxOrderSearchRepository::save)
            .map(trxOrderMapper::toDto);
    }

    @Override
    public Mono<TrxOrderDTO> partialUpdate(TrxOrderDTO trxOrderDTO) {
        log.debug("Request to partially update TrxOrder : {}", trxOrderDTO);

        return trxOrderRepository
            .findById(trxOrderDTO.getId())
            .map(existingTrxOrder -> {
                trxOrderMapper.partialUpdate(existingTrxOrder, trxOrderDTO);

                return existingTrxOrder;
            })
            .flatMap(trxOrderRepository::save)
            .flatMap(savedTrxOrder -> {
                trxOrderSearchRepository.save(savedTrxOrder);
                return Mono.just(savedTrxOrder);
            })
            .map(trxOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxOrderDTO> findByCriteria(TrxOrderCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxOrders by Criteria");
        return trxOrderRepository.findByCriteria(criteria, pageable).map(trxOrderMapper::toDto);
    }

    /**
     * Find the count of trxOrders by criteria.
     * @param criteria filtering criteria
     * @return the count of trxOrders
     */
    public Mono<Long> countByCriteria(TrxOrderCriteria criteria) {
        log.debug("Request to get the count of all TrxOrders by Criteria");
        return trxOrderRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxOrderRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxOrderSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxOrderDTO> findOne(Long id) {
        log.debug("Request to get TrxOrder : {}", id);
        return trxOrderRepository.findById(id).map(trxOrderMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxOrder : {}", id);
        return trxOrderRepository.deleteById(id).then(trxOrderSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxOrderDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxOrders for query {}", query);
        return trxOrderSearchRepository.search(query, pageable).map(trxOrderMapper::toDto);
    }
}
