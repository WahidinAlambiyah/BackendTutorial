package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstSubDistrictCriteria;
import com.mycompany.myapp.service.dto.MstSubDistrictDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstSubDistrict}.
 */
public interface MstSubDistrictService {
    /**
     * Save a mstSubDistrict.
     *
     * @param mstSubDistrictDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstSubDistrictDTO> save(MstSubDistrictDTO mstSubDistrictDTO);

    /**
     * Updates a mstSubDistrict.
     *
     * @param mstSubDistrictDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstSubDistrictDTO> update(MstSubDistrictDTO mstSubDistrictDTO);

    /**
     * Partially updates a mstSubDistrict.
     *
     * @param mstSubDistrictDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstSubDistrictDTO> partialUpdate(MstSubDistrictDTO mstSubDistrictDTO);
    /**
     * Find mstSubDistricts by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstSubDistrictDTO> findByCriteria(MstSubDistrictCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstSubDistricts by criteria.
     * @param criteria filtering criteria
     * @return the count of mstSubDistricts
     */
    public Mono<Long> countByCriteria(MstSubDistrictCriteria criteria);

    /**
     * Get all the mstSubDistricts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstSubDistrictDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of mstSubDistricts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstSubDistricts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstSubDistrict.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstSubDistrictDTO> findOne(Long id);

    /**
     * Delete the "id" mstSubDistrict.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstSubDistrict corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstSubDistrictDTO> search(String query, Pageable pageable);
}
