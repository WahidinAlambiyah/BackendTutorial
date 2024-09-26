package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstDistrictCriteria;
import com.mycompany.myapp.service.dto.MstDistrictDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstDistrict}.
 */
public interface MstDistrictService {
    /**
     * Save a mstDistrict.
     *
     * @param mstDistrictDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstDistrictDTO> save(MstDistrictDTO mstDistrictDTO);

    /**
     * Updates a mstDistrict.
     *
     * @param mstDistrictDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstDistrictDTO> update(MstDistrictDTO mstDistrictDTO);

    /**
     * Partially updates a mstDistrict.
     *
     * @param mstDistrictDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstDistrictDTO> partialUpdate(MstDistrictDTO mstDistrictDTO);
    /**
     * Find mstDistricts by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstDistrictDTO> findByCriteria(MstDistrictCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstDistricts by criteria.
     * @param criteria filtering criteria
     * @return the count of mstDistricts
     */
    public Mono<Long> countByCriteria(MstDistrictCriteria criteria);

    /**
     * Get all the mstDistricts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstDistrictDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of mstDistricts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstDistricts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstDistrict.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstDistrictDTO> findOne(Long id);

    /**
     * Delete the "id" mstDistrict.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstDistrict corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstDistrictDTO> search(String query, Pageable pageable);
}
