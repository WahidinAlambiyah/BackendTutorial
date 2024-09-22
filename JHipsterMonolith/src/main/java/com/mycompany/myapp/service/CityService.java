package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.CityCriteria;
import com.mycompany.myapp.service.dto.CityDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.City}.
 */
public interface CityService {
    /**
     * Save a city.
     *
     * @param cityDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CityDTO> save(CityDTO cityDTO);

    /**
     * Updates a city.
     *
     * @param cityDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CityDTO> update(CityDTO cityDTO);

    /**
     * Partially updates a city.
     *
     * @param cityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CityDTO> partialUpdate(CityDTO cityDTO);
    /**
     * Find cities by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CityDTO> findByCriteria(CityCriteria criteria, Pageable pageable);

    /**
     * Find the count of cities by criteria.
     * @param criteria filtering criteria
     * @return the count of cities
     */
    public Mono<Long> countByCriteria(CityCriteria criteria);

    /**
     * Get all the cities with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CityDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of cities available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of cities available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" city.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CityDTO> findOne(Long id);

    /**
     * Delete the "id" city.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the city corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CityDTO> search(String query, Pageable pageable);
}
