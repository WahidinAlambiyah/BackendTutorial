package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Province;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Province}.
 */
public interface ProvinceService {
    /**
     * Save a province.
     *
     * @param province the entity to save.
     * @return the persisted entity.
     */
    Mono<Province> save(Province province);

    /**
     * Updates a province.
     *
     * @param province the entity to update.
     * @return the persisted entity.
     */
    Mono<Province> update(Province province);

    /**
     * Partially updates a province.
     *
     * @param province the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Province> partialUpdate(Province province);

    /**
     * Get all the provinces.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Province> findAll(Pageable pageable);

    /**
     * Get all the provinces with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Province> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of provinces available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of provinces available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" province.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Province> findOne(Long id);

    /**
     * Delete the "id" province.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the province corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Province> search(String query, Pageable pageable);
}
