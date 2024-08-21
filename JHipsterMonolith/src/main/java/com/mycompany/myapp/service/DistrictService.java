package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.DistrictCriteria;
import com.mycompany.myapp.service.dto.DistrictDTO;
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
     * @param districtDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DistrictDTO> save(DistrictDTO districtDTO);

    /**
     * Updates a district.
     *
     * @param districtDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DistrictDTO> update(DistrictDTO districtDTO);

    /**
     * Partially updates a district.
     *
     * @param districtDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DistrictDTO> partialUpdate(DistrictDTO districtDTO);
    /**
     * Find districts by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DistrictDTO> findByCriteria(DistrictCriteria criteria, Pageable pageable);

    /**
     * Find the count of districts by criteria.
     * @param criteria filtering criteria
     * @return the count of districts
     */
    public Mono<Long> countByCriteria(DistrictCriteria criteria);

    /**
     * Get all the districts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DistrictDTO> findAllWithEagerRelationships(Pageable pageable);

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
    Mono<DistrictDTO> findOne(Long id);

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
    Flux<DistrictDTO> search(String query, Pageable pageable);
}
