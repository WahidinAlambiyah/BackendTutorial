package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.EmployeeCriteria;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Employee}.
 */
public interface EmployeeService {
    /**
     * Save a employee.
     *
     * @param employeeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EmployeeDTO> save(EmployeeDTO employeeDTO);

    /**
     * Updates a employee.
     *
     * @param employeeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EmployeeDTO> update(EmployeeDTO employeeDTO);

    /**
     * Partially updates a employee.
     *
     * @param employeeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EmployeeDTO> partialUpdate(EmployeeDTO employeeDTO);
    /**
     * Find employees by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EmployeeDTO> findByCriteria(EmployeeCriteria criteria, Pageable pageable);

    /**
     * Find the count of employees by criteria.
     * @param criteria filtering criteria
     * @return the count of employees
     */
    public Mono<Long> countByCriteria(EmployeeCriteria criteria);

    /**
     * Get all the EmployeeDTO where JobHistory is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<EmployeeDTO> findAllWhereJobHistoryIsNull();

    /**
     * Returns the number of employees available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of employees available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" employee.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EmployeeDTO> findOne(Long id);

    /**
     * Delete the "id" employee.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the employee corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EmployeeDTO> search(String query, Pageable pageable);
}
