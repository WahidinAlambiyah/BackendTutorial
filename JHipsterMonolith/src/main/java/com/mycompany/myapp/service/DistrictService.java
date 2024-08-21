package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.District;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.District}.
 */
public interface DistrictService {
    /**
     * Save a district.
     *
     * @param district the entity to save.
     * @return the persisted entity.
     */
    Mono<District> save(District district);

    /**
     * Updates a district.
     *
     * @param district the entity to update.
     * @return the persisted entity.
     */
    Mono<District> update(District district);

    /**
     * Partially updates a district.
     *
     * @param district the entity to update partially.
     * @return the persisted entity.
     */
    Mono<District> partialUpdate(District district);

    /**
     * Get all the districts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<District> findAll(Pageable pageable);

    /**
     * Get all the districts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<District> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of districts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of districts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" district.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<District> findOne(Long id);

    /**
     * Delete the "id" district.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the district corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<District> search(String query, Pageable pageable);
}
