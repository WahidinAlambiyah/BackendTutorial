package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxOrderStockCriteria;
import com.mycompany.myapp.service.dto.TrxOrderStockDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxOrderStock}.
 */
public interface TrxOrderStockService {
    /**
     * Save a trxOrderStock.
     *
     * @param trxOrderStockDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxOrderStockDTO> save(TrxOrderStockDTO trxOrderStockDTO);

    /**
     * Updates a trxOrderStock.
     *
     * @param trxOrderStockDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxOrderStockDTO> update(TrxOrderStockDTO trxOrderStockDTO);

    /**
     * Partially updates a trxOrderStock.
     *
     * @param trxOrderStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxOrderStockDTO> partialUpdate(TrxOrderStockDTO trxOrderStockDTO);
    /**
     * Find trxOrderStocks by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxOrderStockDTO> findByCriteria(TrxOrderStockCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxOrderStocks by criteria.
     * @param criteria filtering criteria
     * @return the count of trxOrderStocks
     */
    public Mono<Long> countByCriteria(TrxOrderStockCriteria criteria);

    /**
     * Returns the number of trxOrderStocks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxOrderStocks available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxOrderStock.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxOrderStockDTO> findOne(Long id);

    /**
     * Delete the "id" trxOrderStock.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxOrderStock corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxOrderStockDTO> search(String query, Pageable pageable);
}
