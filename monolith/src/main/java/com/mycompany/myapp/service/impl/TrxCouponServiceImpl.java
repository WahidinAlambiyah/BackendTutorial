package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxCouponCriteria;
import com.mycompany.myapp.repository.TrxCouponRepository;
import com.mycompany.myapp.repository.search.TrxCouponSearchRepository;
import com.mycompany.myapp.service.TrxCouponService;
import com.mycompany.myapp.service.dto.TrxCouponDTO;
import com.mycompany.myapp.service.mapper.TrxCouponMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxCoupon}.
 */
@Service
@Transactional
public class TrxCouponServiceImpl implements TrxCouponService {

    private static final Logger log = LoggerFactory.getLogger(TrxCouponServiceImpl.class);

    private final TrxCouponRepository trxCouponRepository;

    private final TrxCouponMapper trxCouponMapper;

    private final TrxCouponSearchRepository trxCouponSearchRepository;

    public TrxCouponServiceImpl(
        TrxCouponRepository trxCouponRepository,
        TrxCouponMapper trxCouponMapper,
        TrxCouponSearchRepository trxCouponSearchRepository
    ) {
        this.trxCouponRepository = trxCouponRepository;
        this.trxCouponMapper = trxCouponMapper;
        this.trxCouponSearchRepository = trxCouponSearchRepository;
    }

    @Override
    public Mono<TrxCouponDTO> save(TrxCouponDTO trxCouponDTO) {
        log.debug("Request to save TrxCoupon : {}", trxCouponDTO);
        return trxCouponRepository
            .save(trxCouponMapper.toEntity(trxCouponDTO))
            .flatMap(trxCouponSearchRepository::save)
            .map(trxCouponMapper::toDto);
    }

    @Override
    public Mono<TrxCouponDTO> update(TrxCouponDTO trxCouponDTO) {
        log.debug("Request to update TrxCoupon : {}", trxCouponDTO);
        return trxCouponRepository
            .save(trxCouponMapper.toEntity(trxCouponDTO))
            .flatMap(trxCouponSearchRepository::save)
            .map(trxCouponMapper::toDto);
    }

    @Override
    public Mono<TrxCouponDTO> partialUpdate(TrxCouponDTO trxCouponDTO) {
        log.debug("Request to partially update TrxCoupon : {}", trxCouponDTO);

        return trxCouponRepository
            .findById(trxCouponDTO.getId())
            .map(existingTrxCoupon -> {
                trxCouponMapper.partialUpdate(existingTrxCoupon, trxCouponDTO);

                return existingTrxCoupon;
            })
            .flatMap(trxCouponRepository::save)
            .flatMap(savedTrxCoupon -> {
                trxCouponSearchRepository.save(savedTrxCoupon);
                return Mono.just(savedTrxCoupon);
            })
            .map(trxCouponMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxCouponDTO> findByCriteria(TrxCouponCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxCoupons by Criteria");
        return trxCouponRepository.findByCriteria(criteria, pageable).map(trxCouponMapper::toDto);
    }

    /**
     * Find the count of trxCoupons by criteria.
     * @param criteria filtering criteria
     * @return the count of trxCoupons
     */
    public Mono<Long> countByCriteria(TrxCouponCriteria criteria) {
        log.debug("Request to get the count of all TrxCoupons by Criteria");
        return trxCouponRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxCouponRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxCouponSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxCouponDTO> findOne(Long id) {
        log.debug("Request to get TrxCoupon : {}", id);
        return trxCouponRepository.findById(id).map(trxCouponMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxCoupon : {}", id);
        return trxCouponRepository.deleteById(id).then(trxCouponSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxCouponDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxCoupons for query {}", query);
        return trxCouponSearchRepository.search(query, pageable).map(trxCouponMapper::toDto);
    }
}
