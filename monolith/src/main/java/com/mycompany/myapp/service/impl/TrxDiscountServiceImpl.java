package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxDiscountCriteria;
import com.mycompany.myapp.repository.TrxDiscountRepository;
import com.mycompany.myapp.repository.search.TrxDiscountSearchRepository;
import com.mycompany.myapp.service.TrxDiscountService;
import com.mycompany.myapp.service.dto.TrxDiscountDTO;
import com.mycompany.myapp.service.mapper.TrxDiscountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxDiscount}.
 */
@Service
@Transactional
public class TrxDiscountServiceImpl implements TrxDiscountService {

    private static final Logger log = LoggerFactory.getLogger(TrxDiscountServiceImpl.class);

    private final TrxDiscountRepository trxDiscountRepository;

    private final TrxDiscountMapper trxDiscountMapper;

    private final TrxDiscountSearchRepository trxDiscountSearchRepository;

    public TrxDiscountServiceImpl(
        TrxDiscountRepository trxDiscountRepository,
        TrxDiscountMapper trxDiscountMapper,
        TrxDiscountSearchRepository trxDiscountSearchRepository
    ) {
        this.trxDiscountRepository = trxDiscountRepository;
        this.trxDiscountMapper = trxDiscountMapper;
        this.trxDiscountSearchRepository = trxDiscountSearchRepository;
    }

    @Override
    public Mono<TrxDiscountDTO> save(TrxDiscountDTO trxDiscountDTO) {
        log.debug("Request to save TrxDiscount : {}", trxDiscountDTO);
        return trxDiscountRepository
            .save(trxDiscountMapper.toEntity(trxDiscountDTO))
            .flatMap(trxDiscountSearchRepository::save)
            .map(trxDiscountMapper::toDto);
    }

    @Override
    public Mono<TrxDiscountDTO> update(TrxDiscountDTO trxDiscountDTO) {
        log.debug("Request to update TrxDiscount : {}", trxDiscountDTO);
        return trxDiscountRepository
            .save(trxDiscountMapper.toEntity(trxDiscountDTO))
            .flatMap(trxDiscountSearchRepository::save)
            .map(trxDiscountMapper::toDto);
    }

    @Override
    public Mono<TrxDiscountDTO> partialUpdate(TrxDiscountDTO trxDiscountDTO) {
        log.debug("Request to partially update TrxDiscount : {}", trxDiscountDTO);

        return trxDiscountRepository
            .findById(trxDiscountDTO.getId())
            .map(existingTrxDiscount -> {
                trxDiscountMapper.partialUpdate(existingTrxDiscount, trxDiscountDTO);

                return existingTrxDiscount;
            })
            .flatMap(trxDiscountRepository::save)
            .flatMap(savedTrxDiscount -> {
                trxDiscountSearchRepository.save(savedTrxDiscount);
                return Mono.just(savedTrxDiscount);
            })
            .map(trxDiscountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxDiscountDTO> findByCriteria(TrxDiscountCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxDiscounts by Criteria");
        return trxDiscountRepository.findByCriteria(criteria, pageable).map(trxDiscountMapper::toDto);
    }

    /**
     * Find the count of trxDiscounts by criteria.
     * @param criteria filtering criteria
     * @return the count of trxDiscounts
     */
    public Mono<Long> countByCriteria(TrxDiscountCriteria criteria) {
        log.debug("Request to get the count of all TrxDiscounts by Criteria");
        return trxDiscountRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxDiscountRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxDiscountSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxDiscountDTO> findOne(Long id) {
        log.debug("Request to get TrxDiscount : {}", id);
        return trxDiscountRepository.findById(id).map(trxDiscountMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxDiscount : {}", id);
        return trxDiscountRepository.deleteById(id).then(trxDiscountSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxDiscountDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxDiscounts for query {}", query);
        return trxDiscountSearchRepository.search(query, pageable).map(trxDiscountMapper::toDto);
    }
}
