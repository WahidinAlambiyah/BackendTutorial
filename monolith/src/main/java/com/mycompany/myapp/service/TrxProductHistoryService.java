package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxProductHistoryCriteria;
import com.mycompany.myapp.service.dto.TrxProductHistoryDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxProductHistory}.
 */
public interface TrxProductHistoryService {
    /**
     * Save a trxProductHistory.
     *
     * @param trxProductHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxProductHistoryDTO> save(TrxProductHistoryDTO trxProductHistoryDTO);

    /**
     * Updates a trxProductHistory.
     *
     * @param trxProductHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxProductHistoryDTO> update(TrxProductHistoryDTO trxProductHistoryDTO);

    /**
     * Partially updates a trxProductHistory.
     *
     * @param trxProductHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxProductHistoryDTO> partialUpdate(TrxProductHistoryDTO trxProductHistoryDTO);
    /**
     * Find trxProductHistories by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxProductHistoryDTO> findByCriteria(TrxProductHistoryCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxProductHistories by criteria.
     * @param criteria filtering criteria
     * @return the count of trxProductHistories
     */
    public Mono<Long> countByCriteria(TrxProductHistoryCriteria criteria);

    /**
     * Returns the number of trxProductHistories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxProductHistories available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxProductHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxProductHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" trxProductHistory.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxProductHistory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxProductHistoryDTO> search(String query, Pageable pageable);
}
