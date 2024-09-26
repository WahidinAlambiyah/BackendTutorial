package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstDepartmentCriteria;
import com.mycompany.myapp.service.dto.MstDepartmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstDepartment}.
 */
public interface MstDepartmentService {
    /**
     * Save a mstDepartment.
     *
     * @param mstDepartmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstDepartmentDTO> save(MstDepartmentDTO mstDepartmentDTO);

    /**
     * Updates a mstDepartment.
     *
     * @param mstDepartmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstDepartmentDTO> update(MstDepartmentDTO mstDepartmentDTO);

    /**
     * Partially updates a mstDepartment.
     *
     * @param mstDepartmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstDepartmentDTO> partialUpdate(MstDepartmentDTO mstDepartmentDTO);
    /**
     * Find mstDepartments by criteria.
     *
     * @return the list of entities.
     */
    Flux<MstDepartmentDTO> findByCriteria(MstDepartmentCriteria criteria);

    /**
     * Find the count of mstDepartments by criteria.
     * @param criteria filtering criteria
     * @return the count of mstDepartments
     */
    public Mono<Long> countByCriteria(MstDepartmentCriteria criteria);

    /**
     * Get all the MstDepartmentDTO where JobHistory is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<MstDepartmentDTO> findAllWhereJobHistoryIsNull();

    /**
     * Returns the number of mstDepartments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstDepartments available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstDepartment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstDepartmentDTO> findOne(Long id);

    /**
     * Delete the "id" mstDepartment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstDepartment corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<MstDepartmentDTO> search(String query);
}
