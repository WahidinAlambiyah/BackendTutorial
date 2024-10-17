package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxDiscountCriteria;
import com.mycompany.myapp.service.dto.TrxDiscountDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxDiscount}.
 */
public interface TrxDiscountService {
    /**
     * Save a trxDiscount.
     *
     * @param trxDiscountDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxDiscountDTO> save(TrxDiscountDTO trxDiscountDTO);

    /**
     * Updates a trxDiscount.
     *
     * @param trxDiscountDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxDiscountDTO> update(TrxDiscountDTO trxDiscountDTO);

    /**
     * Partially updates a trxDiscount.
     *
     * @param trxDiscountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxDiscountDTO> partialUpdate(TrxDiscountDTO trxDiscountDTO);
    /**
     * Find trxDiscounts by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxDiscountDTO> findByCriteria(TrxDiscountCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxDiscounts by criteria.
     * @param criteria filtering criteria
     * @return the count of trxDiscounts
     */
    public Mono<Long> countByCriteria(TrxDiscountCriteria criteria);

    /**
     * Returns the number of trxDiscounts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxDiscounts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxDiscount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxDiscountDTO> findOne(Long id);

    /**
     * Delete the "id" trxDiscount.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxDiscount corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxDiscountDTO> search(String query, Pageable pageable);
}
