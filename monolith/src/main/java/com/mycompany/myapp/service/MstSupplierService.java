package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstSupplierCriteria;
import com.mycompany.myapp.service.dto.MstSupplierDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstSupplier}.
 */
public interface MstSupplierService {
    /**
     * Save a mstSupplier.
     *
     * @param mstSupplierDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstSupplierDTO> save(MstSupplierDTO mstSupplierDTO);

    /**
     * Updates a mstSupplier.
     *
     * @param mstSupplierDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstSupplierDTO> update(MstSupplierDTO mstSupplierDTO);

    /**
     * Partially updates a mstSupplier.
     *
     * @param mstSupplierDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstSupplierDTO> partialUpdate(MstSupplierDTO mstSupplierDTO);
    /**
     * Find mstSuppliers by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstSupplierDTO> findByCriteria(MstSupplierCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstSuppliers by criteria.
     * @param criteria filtering criteria
     * @return the count of mstSuppliers
     */
    public Mono<Long> countByCriteria(MstSupplierCriteria criteria);

    /**
     * Returns the number of mstSuppliers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstSuppliers available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstSupplier.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstSupplierDTO> findOne(Long id);

    /**
     * Delete the "id" mstSupplier.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstSupplier corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstSupplierDTO> search(String query, Pageable pageable);
}
