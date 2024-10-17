package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxCartCriteria;
import com.mycompany.myapp.service.dto.TrxCartDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxCart}.
 */
public interface TrxCartService {
    /**
     * Save a trxCart.
     *
     * @param trxCartDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxCartDTO> save(TrxCartDTO trxCartDTO);

    /**
     * Updates a trxCart.
     *
     * @param trxCartDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxCartDTO> update(TrxCartDTO trxCartDTO);

    /**
     * Partially updates a trxCart.
     *
     * @param trxCartDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxCartDTO> partialUpdate(TrxCartDTO trxCartDTO);
    /**
     * Find trxCarts by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxCartDTO> findByCriteria(TrxCartCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxCarts by criteria.
     * @param criteria filtering criteria
     * @return the count of trxCarts
     */
    public Mono<Long> countByCriteria(TrxCartCriteria criteria);

    /**
     * Returns the number of trxCarts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxCarts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxCart.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxCartDTO> findOne(Long id);

    /**
     * Delete the "id" trxCart.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxCart corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxCartDTO> search(String query, Pageable pageable);
}
