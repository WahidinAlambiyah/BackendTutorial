package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstDriverCriteria;
import com.mycompany.myapp.service.dto.MstDriverDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstDriver}.
 */
public interface MstDriverService {
    /**
     * Save a mstDriver.
     *
     * @param mstDriverDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstDriverDTO> save(MstDriverDTO mstDriverDTO);

    /**
     * Updates a mstDriver.
     *
     * @param mstDriverDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstDriverDTO> update(MstDriverDTO mstDriverDTO);

    /**
     * Partially updates a mstDriver.
     *
     * @param mstDriverDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstDriverDTO> partialUpdate(MstDriverDTO mstDriverDTO);
    /**
     * Find mstDrivers by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstDriverDTO> findByCriteria(MstDriverCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstDrivers by criteria.
     * @param criteria filtering criteria
     * @return the count of mstDrivers
     */
    public Mono<Long> countByCriteria(MstDriverCriteria criteria);

    /**
     * Returns the number of mstDrivers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstDrivers available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstDriver.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstDriverDTO> findOne(Long id);

    /**
     * Delete the "id" mstDriver.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstDriver corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstDriverDTO> search(String query, Pageable pageable);
}
