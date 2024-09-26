package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstRegionCriteria;
import com.mycompany.myapp.service.dto.MstRegionDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstRegion}.
 */
public interface MstRegionService {
    /**
     * Save a mstRegion.
     *
     * @param mstRegionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstRegionDTO> save(MstRegionDTO mstRegionDTO);

    /**
     * Updates a mstRegion.
     *
     * @param mstRegionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstRegionDTO> update(MstRegionDTO mstRegionDTO);

    /**
     * Partially updates a mstRegion.
     *
     * @param mstRegionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstRegionDTO> partialUpdate(MstRegionDTO mstRegionDTO);
    /**
     * Find mstRegions by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstRegionDTO> findByCriteria(MstRegionCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstRegions by criteria.
     * @param criteria filtering criteria
     * @return the count of mstRegions
     */
    public Mono<Long> countByCriteria(MstRegionCriteria criteria);

    /**
     * Returns the number of mstRegions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstRegions available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstRegion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstRegionDTO> findOne(Long id);

    /**
     * Delete the "id" mstRegion.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstRegion corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstRegionDTO> search(String query, Pageable pageable);
}
