package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxOrderItemCriteria;
import com.mycompany.myapp.service.dto.TrxOrderItemDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxOrderItem}.
 */
public interface TrxOrderItemService {
    /**
     * Save a trxOrderItem.
     *
     * @param trxOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxOrderItemDTO> save(TrxOrderItemDTO trxOrderItemDTO);

    /**
     * Updates a trxOrderItem.
     *
     * @param trxOrderItemDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxOrderItemDTO> update(TrxOrderItemDTO trxOrderItemDTO);

    /**
     * Partially updates a trxOrderItem.
     *
     * @param trxOrderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxOrderItemDTO> partialUpdate(TrxOrderItemDTO trxOrderItemDTO);
    /**
     * Find trxOrderItems by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxOrderItemDTO> findByCriteria(TrxOrderItemCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxOrderItems by criteria.
     * @param criteria filtering criteria
     * @return the count of trxOrderItems
     */
    public Mono<Long> countByCriteria(TrxOrderItemCriteria criteria);

    /**
     * Returns the number of trxOrderItems available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxOrderItems available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxOrderItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxOrderItemDTO> findOne(Long id);

    /**
     * Delete the "id" trxOrderItem.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxOrderItem corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxOrderItemDTO> search(String query, Pageable pageable);
}
