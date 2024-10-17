package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxOrderItemCriteria;
import com.mycompany.myapp.repository.TrxOrderItemRepository;
import com.mycompany.myapp.repository.search.TrxOrderItemSearchRepository;
import com.mycompany.myapp.service.TrxOrderItemService;
import com.mycompany.myapp.service.dto.TrxOrderItemDTO;
import com.mycompany.myapp.service.mapper.TrxOrderItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxOrderItem}.
 */
@Service
@Transactional
public class TrxOrderItemServiceImpl implements TrxOrderItemService {

    private static final Logger log = LoggerFactory.getLogger(TrxOrderItemServiceImpl.class);

    private final TrxOrderItemRepository trxOrderItemRepository;

    private final TrxOrderItemMapper trxOrderItemMapper;

    private final TrxOrderItemSearchRepository trxOrderItemSearchRepository;

    public TrxOrderItemServiceImpl(
        TrxOrderItemRepository trxOrderItemRepository,
        TrxOrderItemMapper trxOrderItemMapper,
        TrxOrderItemSearchRepository trxOrderItemSearchRepository
    ) {
        this.trxOrderItemRepository = trxOrderItemRepository;
        this.trxOrderItemMapper = trxOrderItemMapper;
        this.trxOrderItemSearchRepository = trxOrderItemSearchRepository;
    }

    @Override
    public Mono<TrxOrderItemDTO> save(TrxOrderItemDTO trxOrderItemDTO) {
        log.debug("Request to save TrxOrderItem : {}", trxOrderItemDTO);
        return trxOrderItemRepository
            .save(trxOrderItemMapper.toEntity(trxOrderItemDTO))
            .flatMap(trxOrderItemSearchRepository::save)
            .map(trxOrderItemMapper::toDto);
    }

    @Override
    public Mono<TrxOrderItemDTO> update(TrxOrderItemDTO trxOrderItemDTO) {
        log.debug("Request to update TrxOrderItem : {}", trxOrderItemDTO);
        return trxOrderItemRepository
            .save(trxOrderItemMapper.toEntity(trxOrderItemDTO))
            .flatMap(trxOrderItemSearchRepository::save)
            .map(trxOrderItemMapper::toDto);
    }

    @Override
    public Mono<TrxOrderItemDTO> partialUpdate(TrxOrderItemDTO trxOrderItemDTO) {
        log.debug("Request to partially update TrxOrderItem : {}", trxOrderItemDTO);

        return trxOrderItemRepository
            .findById(trxOrderItemDTO.getId())
            .map(existingTrxOrderItem -> {
                trxOrderItemMapper.partialUpdate(existingTrxOrderItem, trxOrderItemDTO);

                return existingTrxOrderItem;
            })
            .flatMap(trxOrderItemRepository::save)
            .flatMap(savedTrxOrderItem -> {
                trxOrderItemSearchRepository.save(savedTrxOrderItem);
                return Mono.just(savedTrxOrderItem);
            })
            .map(trxOrderItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxOrderItemDTO> findByCriteria(TrxOrderItemCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxOrderItems by Criteria");
        return trxOrderItemRepository.findByCriteria(criteria, pageable).map(trxOrderItemMapper::toDto);
    }

    /**
     * Find the count of trxOrderItems by criteria.
     * @param criteria filtering criteria
     * @return the count of trxOrderItems
     */
    public Mono<Long> countByCriteria(TrxOrderItemCriteria criteria) {
        log.debug("Request to get the count of all TrxOrderItems by Criteria");
        return trxOrderItemRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxOrderItemRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxOrderItemSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxOrderItemDTO> findOne(Long id) {
        log.debug("Request to get TrxOrderItem : {}", id);
        return trxOrderItemRepository.findById(id).map(trxOrderItemMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxOrderItem : {}", id);
        return trxOrderItemRepository.deleteById(id).then(trxOrderItemSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxOrderItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxOrderItems for query {}", query);
        return trxOrderItemSearchRepository.search(query, pageable).map(trxOrderItemMapper::toDto);
    }
}
