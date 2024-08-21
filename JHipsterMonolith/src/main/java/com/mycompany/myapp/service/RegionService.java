package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.RegionCriteria;
import com.mycompany.myapp.service.dto.RegionDTO;
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
     * @param regionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RegionDTO> save(RegionDTO regionDTO);

    /**
     * Updates a region.
     *
     * @param regionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RegionDTO> update(RegionDTO regionDTO);

    /**
     * Partially updates a region.
     *
     * @param regionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RegionDTO> partialUpdate(RegionDTO regionDTO);
    /**
     * Find regions by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RegionDTO> findByCriteria(RegionCriteria criteria, Pageable pageable);

    /**
     * Find the count of regions by criteria.
     * @param criteria filtering criteria
     * @return the count of regions
     */
    public Mono<Long> countByCriteria(RegionCriteria criteria);

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
    Mono<RegionDTO> findOne(Long id);

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
    Flux<RegionDTO> search(String query, Pageable pageable);
}
