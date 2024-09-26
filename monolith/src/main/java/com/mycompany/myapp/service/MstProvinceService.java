package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstProvinceCriteria;
import com.mycompany.myapp.service.dto.MstProvinceDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstProvince}.
 */
public interface MstProvinceService {
    /**
     * Save a mstProvince.
     *
     * @param mstProvinceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstProvinceDTO> save(MstProvinceDTO mstProvinceDTO);

    /**
     * Updates a mstProvince.
     *
     * @param mstProvinceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstProvinceDTO> update(MstProvinceDTO mstProvinceDTO);

    /**
     * Partially updates a mstProvince.
     *
     * @param mstProvinceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstProvinceDTO> partialUpdate(MstProvinceDTO mstProvinceDTO);
    /**
     * Find mstProvinces by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstProvinceDTO> findByCriteria(MstProvinceCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstProvinces by criteria.
     * @param criteria filtering criteria
     * @return the count of mstProvinces
     */
    public Mono<Long> countByCriteria(MstProvinceCriteria criteria);

    /**
     * Get all the mstProvinces with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstProvinceDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of mstProvinces available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstProvinces available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstProvince.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstProvinceDTO> findOne(Long id);

    /**
     * Delete the "id" mstProvince.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstProvince corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstProvinceDTO> search(String query, Pageable pageable);
}
