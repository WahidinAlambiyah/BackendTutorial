package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxCouponCriteria;
import com.mycompany.myapp.service.dto.TrxCouponDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxCoupon}.
 */
public interface TrxCouponService {
    /**
     * Save a trxCoupon.
     *
     * @param trxCouponDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxCouponDTO> save(TrxCouponDTO trxCouponDTO);

    /**
     * Updates a trxCoupon.
     *
     * @param trxCouponDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxCouponDTO> update(TrxCouponDTO trxCouponDTO);

    /**
     * Partially updates a trxCoupon.
     *
     * @param trxCouponDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxCouponDTO> partialUpdate(TrxCouponDTO trxCouponDTO);
    /**
     * Find trxCoupons by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxCouponDTO> findByCriteria(TrxCouponCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxCoupons by criteria.
     * @param criteria filtering criteria
     * @return the count of trxCoupons
     */
    public Mono<Long> countByCriteria(TrxCouponCriteria criteria);

    /**
     * Returns the number of trxCoupons available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxCoupons available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxCoupon.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxCouponDTO> findOne(Long id);

    /**
     * Delete the "id" trxCoupon.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxCoupon corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxCouponDTO> search(String query, Pageable pageable);
}
