package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstBrandCriteria;
import com.mycompany.myapp.service.dto.MstBrandDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstBrand}.
 */
public interface MstBrandService {
    /**
     * Save a mstBrand.
     *
     * @param mstBrandDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstBrandDTO> save(MstBrandDTO mstBrandDTO);

    /**
     * Updates a mstBrand.
     *
     * @param mstBrandDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstBrandDTO> update(MstBrandDTO mstBrandDTO);

    /**
     * Partially updates a mstBrand.
     *
     * @param mstBrandDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstBrandDTO> partialUpdate(MstBrandDTO mstBrandDTO);
    /**
     * Find mstBrands by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstBrandDTO> findByCriteria(MstBrandCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstBrands by criteria.
     * @param criteria filtering criteria
     * @return the count of mstBrands
     */
    public Mono<Long> countByCriteria(MstBrandCriteria criteria);

    /**
     * Returns the number of mstBrands available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstBrands available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstBrand.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstBrandDTO> findOne(Long id);

    /**
     * Delete the "id" mstBrand.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstBrand corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstBrandDTO> search(String query, Pageable pageable);
}
