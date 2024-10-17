package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstCategoryCriteria;
import com.mycompany.myapp.service.dto.MstCategoryDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstCategory}.
 */
public interface MstCategoryService {
    /**
     * Save a mstCategory.
     *
     * @param mstCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstCategoryDTO> save(MstCategoryDTO mstCategoryDTO);

    /**
     * Updates a mstCategory.
     *
     * @param mstCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstCategoryDTO> update(MstCategoryDTO mstCategoryDTO);

    /**
     * Partially updates a mstCategory.
     *
     * @param mstCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstCategoryDTO> partialUpdate(MstCategoryDTO mstCategoryDTO);
    /**
     * Find mstCategories by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCategoryDTO> findByCriteria(MstCategoryCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstCategories by criteria.
     * @param criteria filtering criteria
     * @return the count of mstCategories
     */
    public Mono<Long> countByCriteria(MstCategoryCriteria criteria);

    /**
     * Returns the number of mstCategories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstCategories available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" mstCategory.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstCategory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCategoryDTO> search(String query, Pageable pageable);
}
