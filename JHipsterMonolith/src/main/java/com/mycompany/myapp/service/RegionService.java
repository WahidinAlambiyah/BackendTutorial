package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Region;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Region}.
 */
public interface RegionService {
    /**
     * Save a region.
     *
     * @param region the entity to save.
     * @return the persisted entity.
     */
    Mono<Region> save(Region region);

    /**
     * Updates a region.
     *
     * @param region the entity to update.
     * @return the persisted entity.
     */
    Mono<Region> update(Region region);

    /**
     * Partially updates a region.
     *
     * @param region the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Region> partialUpdate(Region region);

    /**
     * Get all the regions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Region> findAll(Pageable pageable);

    /**
     * Returns the number of regions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of regions available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" region.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Region> findOne(Long id);

    /**
     * Delete the "id" region.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the region corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Region> search(String query, Pageable pageable);
}
