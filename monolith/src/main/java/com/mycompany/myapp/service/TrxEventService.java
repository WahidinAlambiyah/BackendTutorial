package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxEventCriteria;
import com.mycompany.myapp.service.dto.TrxEventDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxEvent}.
 */
public interface TrxEventService {
    /**
     * Save a trxEvent.
     *
     * @param trxEventDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxEventDTO> save(TrxEventDTO trxEventDTO);

    /**
     * Updates a trxEvent.
     *
     * @param trxEventDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxEventDTO> update(TrxEventDTO trxEventDTO);

    /**
     * Partially updates a trxEvent.
     *
     * @param trxEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxEventDTO> partialUpdate(TrxEventDTO trxEventDTO);
    /**
     * Find trxEvents by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxEventDTO> findByCriteria(TrxEventCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxEvents by criteria.
     * @param criteria filtering criteria
     * @return the count of trxEvents
     */
    public Mono<Long> countByCriteria(TrxEventCriteria criteria);

    /**
     * Returns the number of trxEvents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxEvents available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxEventDTO> findOne(Long id);

    /**
     * Delete the "id" trxEvent.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxEvent corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxEventDTO> search(String query, Pageable pageable);
}
