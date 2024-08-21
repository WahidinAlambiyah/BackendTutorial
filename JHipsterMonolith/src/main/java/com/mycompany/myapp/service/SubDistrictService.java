package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SubDistrict;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.SubDistrict}.
 */
public interface SubDistrictService {
    /**
     * Save a subDistrict.
     *
     * @param subDistrict the entity to save.
     * @return the persisted entity.
     */
    Mono<SubDistrict> save(SubDistrict subDistrict);

    /**
     * Updates a subDistrict.
     *
     * @param subDistrict the entity to update.
     * @return the persisted entity.
     */
    Mono<SubDistrict> update(SubDistrict subDistrict);

    /**
     * Partially updates a subDistrict.
     *
     * @param subDistrict the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SubDistrict> partialUpdate(SubDistrict subDistrict);

    /**
     * Get all the subDistricts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SubDistrict> findAll(Pageable pageable);

    /**
     * Get all the subDistricts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SubDistrict> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of subDistricts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of subDistricts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" subDistrict.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SubDistrict> findOne(Long id);

    /**
     * Delete the "id" subDistrict.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the subDistrict corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SubDistrict> search(String query, Pageable pageable);
}
