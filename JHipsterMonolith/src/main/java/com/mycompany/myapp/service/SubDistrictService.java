package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.SubDistrictCriteria;
import com.mycompany.myapp.service.dto.SubDistrictDTO;
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
     * @param subDistrictDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SubDistrictDTO> save(SubDistrictDTO subDistrictDTO);

    /**
     * Updates a subDistrict.
     *
     * @param subDistrictDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SubDistrictDTO> update(SubDistrictDTO subDistrictDTO);

    /**
     * Partially updates a subDistrict.
     *
     * @param subDistrictDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SubDistrictDTO> partialUpdate(SubDistrictDTO subDistrictDTO);
    /**
     * Find subDistricts by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SubDistrictDTO> findByCriteria(SubDistrictCriteria criteria, Pageable pageable);

    /**
     * Find the count of subDistricts by criteria.
     * @param criteria filtering criteria
     * @return the count of subDistricts
     */
    public Mono<Long> countByCriteria(SubDistrictCriteria criteria);

    /**
     * Get all the subDistricts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SubDistrictDTO> findAllWithEagerRelationships(Pageable pageable);

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
    Mono<SubDistrictDTO> findOne(Long id);

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
    Flux<SubDistrictDTO> search(String query, Pageable pageable);
}
