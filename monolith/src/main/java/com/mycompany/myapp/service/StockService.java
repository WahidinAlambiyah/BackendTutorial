package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.StockCriteria;
import com.mycompany.myapp.service.dto.StockDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Stock}.
 */
public interface StockService {
    /**
     * Save a stock.
     *
     * @param stockDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<StockDTO> save(StockDTO stockDTO);

    /**
     * Updates a stock.
     *
     * @param stockDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<StockDTO> update(StockDTO stockDTO);

    /**
     * Partially updates a stock.
     *
     * @param stockDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<StockDTO> partialUpdate(StockDTO stockDTO);
    /**
     * Find stocks by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<StockDTO> findByCriteria(StockCriteria criteria, Pageable pageable);

    /**
     * Find the count of stocks by criteria.
     * @param criteria filtering criteria
     * @return the count of stocks
     */
    public Mono<Long> countByCriteria(StockCriteria criteria);

    /**
     * Returns the number of stocks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of stocks available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" stock.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<StockDTO> findOne(Long id);

    /**
     * Delete the "id" stock.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the stock corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<StockDTO> search(String query, Pageable pageable);
}
