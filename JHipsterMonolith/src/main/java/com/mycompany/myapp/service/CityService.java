package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.City;
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
     * @param city the entity to save.
     * @return the persisted entity.
     */
    Mono<City> save(City city);

    /**
     * Updates a city.
     *
     * @param city the entity to update.
     * @return the persisted entity.
     */
    Mono<City> update(City city);

    /**
     * Partially updates a city.
     *
     * @param city the entity to update partially.
     * @return the persisted entity.
     */
    Mono<City> partialUpdate(City city);

    /**
     * Get all the cities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<City> findAll(Pageable pageable);

    /**
     * Get all the cities with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<City> findAllWithEagerRelationships(Pageable pageable);

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
    Mono<City> findOne(Long id);

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
    Flux<City> search(String query, Pageable pageable);
}
