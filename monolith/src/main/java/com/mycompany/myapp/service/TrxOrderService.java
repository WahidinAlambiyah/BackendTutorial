package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxOrderCriteria;
import com.mycompany.myapp.service.dto.TrxOrderDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxOrder}.
 */
public interface TrxOrderService {
    /**
     * Save a trxOrder.
     *
     * @param trxOrderDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxOrderDTO> save(TrxOrderDTO trxOrderDTO);

    /**
     * Updates a trxOrder.
     *
     * @param trxOrderDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxOrderDTO> update(TrxOrderDTO trxOrderDTO);

    /**
     * Partially updates a trxOrder.
     *
     * @param trxOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxOrderDTO> partialUpdate(TrxOrderDTO trxOrderDTO);
    /**
     * Find trxOrders by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxOrderDTO> findByCriteria(TrxOrderCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxOrders by criteria.
     * @param criteria filtering criteria
     * @return the count of trxOrders
     */
    public Mono<Long> countByCriteria(TrxOrderCriteria criteria);

    /**
     * Returns the number of trxOrders available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxOrders available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxOrderDTO> findOne(Long id);

    /**
     * Delete the "id" trxOrder.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxOrder corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxOrderDTO> search(String query, Pageable pageable);
}
