package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxOrderHistoryCriteria;
import com.mycompany.myapp.service.dto.TrxOrderHistoryDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxOrderHistory}.
 */
public interface TrxOrderHistoryService {
    /**
     * Save a trxOrderHistory.
     *
     * @param trxOrderHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxOrderHistoryDTO> save(TrxOrderHistoryDTO trxOrderHistoryDTO);

    /**
     * Updates a trxOrderHistory.
     *
     * @param trxOrderHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxOrderHistoryDTO> update(TrxOrderHistoryDTO trxOrderHistoryDTO);

    /**
     * Partially updates a trxOrderHistory.
     *
     * @param trxOrderHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxOrderHistoryDTO> partialUpdate(TrxOrderHistoryDTO trxOrderHistoryDTO);
    /**
     * Find trxOrderHistories by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxOrderHistoryDTO> findByCriteria(TrxOrderHistoryCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxOrderHistories by criteria.
     * @param criteria filtering criteria
     * @return the count of trxOrderHistories
     */
    public Mono<Long> countByCriteria(TrxOrderHistoryCriteria criteria);

    /**
     * Returns the number of trxOrderHistories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxOrderHistories available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxOrderHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxOrderHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" trxOrderHistory.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxOrderHistory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxOrderHistoryDTO> search(String query, Pageable pageable);
}
