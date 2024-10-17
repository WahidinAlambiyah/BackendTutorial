package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxCartCriteria;
import com.mycompany.myapp.repository.TrxCartRepository;
import com.mycompany.myapp.repository.search.TrxCartSearchRepository;
import com.mycompany.myapp.service.TrxCartService;
import com.mycompany.myapp.service.dto.TrxCartDTO;
import com.mycompany.myapp.service.mapper.TrxCartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxCart}.
 */
@Service
@Transactional
public class TrxCartServiceImpl implements TrxCartService {

    private static final Logger log = LoggerFactory.getLogger(TrxCartServiceImpl.class);

    private final TrxCartRepository trxCartRepository;

    private final TrxCartMapper trxCartMapper;

    private final TrxCartSearchRepository trxCartSearchRepository;

    public TrxCartServiceImpl(
        TrxCartRepository trxCartRepository,
        TrxCartMapper trxCartMapper,
        TrxCartSearchRepository trxCartSearchRepository
    ) {
        this.trxCartRepository = trxCartRepository;
        this.trxCartMapper = trxCartMapper;
        this.trxCartSearchRepository = trxCartSearchRepository;
    }

    @Override
    public Mono<TrxCartDTO> save(TrxCartDTO trxCartDTO) {
        log.debug("Request to save TrxCart : {}", trxCartDTO);
        return trxCartRepository.save(trxCartMapper.toEntity(trxCartDTO)).flatMap(trxCartSearchRepository::save).map(trxCartMapper::toDto);
    }

    @Override
    public Mono<TrxCartDTO> update(TrxCartDTO trxCartDTO) {
        log.debug("Request to update TrxCart : {}", trxCartDTO);
        return trxCartRepository.save(trxCartMapper.toEntity(trxCartDTO)).flatMap(trxCartSearchRepository::save).map(trxCartMapper::toDto);
    }

    @Override
    public Mono<TrxCartDTO> partialUpdate(TrxCartDTO trxCartDTO) {
        log.debug("Request to partially update TrxCart : {}", trxCartDTO);

        return trxCartRepository
            .findById(trxCartDTO.getId())
            .map(existingTrxCart -> {
                trxCartMapper.partialUpdate(existingTrxCart, trxCartDTO);

                return existingTrxCart;
            })
            .flatMap(trxCartRepository::save)
            .flatMap(savedTrxCart -> {
                trxCartSearchRepository.save(savedTrxCart);
                return Mono.just(savedTrxCart);
            })
            .map(trxCartMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxCartDTO> findByCriteria(TrxCartCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxCarts by Criteria");
        return trxCartRepository.findByCriteria(criteria, pageable).map(trxCartMapper::toDto);
    }

    /**
     * Find the count of trxCarts by criteria.
     * @param criteria filtering criteria
     * @return the count of trxCarts
     */
    public Mono<Long> countByCriteria(TrxCartCriteria criteria) {
        log.debug("Request to get the count of all TrxCarts by Criteria");
        return trxCartRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxCartRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxCartSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxCartDTO> findOne(Long id) {
        log.debug("Request to get TrxCart : {}", id);
        return trxCartRepository.findById(id).map(trxCartMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxCart : {}", id);
        return trxCartRepository.deleteById(id).then(trxCartSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxCartDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxCarts for query {}", query);
        return trxCartSearchRepository.search(query, pageable).map(trxCartMapper::toDto);
    }
}
