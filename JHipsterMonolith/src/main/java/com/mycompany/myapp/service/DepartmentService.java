package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.DepartmentCriteria;
import com.mycompany.myapp.service.dto.DepartmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Department}.
 */
public interface DepartmentService {
    /**
     * Save a department.
     *
     * @param departmentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DepartmentDTO> save(DepartmentDTO departmentDTO);

    /**
     * Updates a department.
     *
     * @param departmentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DepartmentDTO> update(DepartmentDTO departmentDTO);

    /**
     * Partially updates a department.
     *
     * @param departmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DepartmentDTO> partialUpdate(DepartmentDTO departmentDTO);
    /**
     * Find departments by criteria.
     *
     * @return the list of entities.
     */
    Flux<DepartmentDTO> findByCriteria(DepartmentCriteria criteria);

    /**
     * Find the count of departments by criteria.
     * @param criteria filtering criteria
     * @return the count of departments
     */
    public Mono<Long> countByCriteria(DepartmentCriteria criteria);

    /**
     * Get all the DepartmentDTO where JobHistory is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<DepartmentDTO> findAllWhereJobHistoryIsNull();

    /**
     * Returns the number of departments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of departments available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" department.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DepartmentDTO> findOne(Long id);

    /**
     * Delete the "id" department.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the department corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<DepartmentDTO> search(String query);
}
