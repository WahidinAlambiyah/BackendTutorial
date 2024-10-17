package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstProductCriteria;
import com.mycompany.myapp.service.dto.MstProductDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstProduct}.
 */
public interface MstProductService {
    /**
     * Save a mstProduct.
     *
     * @param mstProductDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstProductDTO> save(MstProductDTO mstProductDTO);

    /**
     * Updates a mstProduct.
     *
     * @param mstProductDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstProductDTO> update(MstProductDTO mstProductDTO);

    /**
     * Partially updates a mstProduct.
     *
     * @param mstProductDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstProductDTO> partialUpdate(MstProductDTO mstProductDTO);
    /**
     * Find mstProducts by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstProductDTO> findByCriteria(MstProductCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstProducts by criteria.
     * @param criteria filtering criteria
     * @return the count of mstProducts
     */
    public Mono<Long> countByCriteria(MstProductCriteria criteria);

    /**
     * Returns the number of mstProducts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstProducts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstProduct.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstProductDTO> findOne(Long id);

    /**
     * Delete the "id" mstProduct.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstProduct corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstProductDTO> search(String query, Pageable pageable);
}
