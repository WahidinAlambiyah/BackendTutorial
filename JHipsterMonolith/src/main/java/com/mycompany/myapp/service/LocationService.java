package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.LocationCriteria;
import com.mycompany.myapp.service.dto.LocationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Location}.
 */
public interface LocationService {
    /**
     * Save a location.
     *
     * @param locationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<LocationDTO> save(LocationDTO locationDTO);

    /**
     * Updates a location.
     *
     * @param locationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<LocationDTO> update(LocationDTO locationDTO);

    /**
     * Partially updates a location.
     *
     * @param locationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<LocationDTO> partialUpdate(LocationDTO locationDTO);
    /**
     * Find locations by criteria.
     *
     * @return the list of entities.
     */
    Flux<LocationDTO> findByCriteria(LocationCriteria criteria);

    /**
     * Find the count of locations by criteria.
     * @param criteria filtering criteria
     * @return the count of locations
     */
    public Mono<Long> countByCriteria(LocationCriteria criteria);

    /**
     * Get all the LocationDTO where Department is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<LocationDTO> findAllWhereDepartmentIsNull();

    /**
     * Returns the number of locations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of locations available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" location.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<LocationDTO> findOne(Long id);

    /**
     * Delete the "id" location.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the location corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<LocationDTO> search(String query);
}
