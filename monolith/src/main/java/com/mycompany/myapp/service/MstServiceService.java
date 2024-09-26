package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstServiceCriteria;
import com.mycompany.myapp.service.dto.MstServiceDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstService}.
 */
public interface MstServiceService {
    /**
     * Save a mstService.
     *
     * @param mstServiceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstServiceDTO> save(MstServiceDTO mstServiceDTO);

    /**
     * Updates a mstService.
     *
     * @param mstServiceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstServiceDTO> update(MstServiceDTO mstServiceDTO);

    /**
     * Partially updates a mstService.
     *
     * @param mstServiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstServiceDTO> partialUpdate(MstServiceDTO mstServiceDTO);
    /**
     * Find mstServices by criteria.
     *
     * @return the list of entities.
     */
    Flux<MstServiceDTO> findByCriteria(MstServiceCriteria criteria);

    /**
     * Find the count of mstServices by criteria.
     * @param criteria filtering criteria
     * @return the count of mstServices
     */
    public Mono<Long> countByCriteria(MstServiceCriteria criteria);

    /**
     * Returns the number of mstServices available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstServices available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstService.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstServiceDTO> findOne(Long id);

    /**
     * Delete the "id" mstService.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstService corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<MstServiceDTO> search(String query);
}
