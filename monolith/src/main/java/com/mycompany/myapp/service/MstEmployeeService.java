package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstEmployeeCriteria;
import com.mycompany.myapp.service.dto.MstEmployeeDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstEmployee}.
 */
public interface MstEmployeeService {
    /**
     * Save a mstEmployee.
     *
     * @param mstEmployeeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstEmployeeDTO> save(MstEmployeeDTO mstEmployeeDTO);

    /**
     * Updates a mstEmployee.
     *
     * @param mstEmployeeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstEmployeeDTO> update(MstEmployeeDTO mstEmployeeDTO);

    /**
     * Partially updates a mstEmployee.
     *
     * @param mstEmployeeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstEmployeeDTO> partialUpdate(MstEmployeeDTO mstEmployeeDTO);
    /**
     * Find mstEmployees by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstEmployeeDTO> findByCriteria(MstEmployeeCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstEmployees by criteria.
     * @param criteria filtering criteria
     * @return the count of mstEmployees
     */
    public Mono<Long> countByCriteria(MstEmployeeCriteria criteria);

    /**
     * Get all the MstEmployeeDTO where JobHistory is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<MstEmployeeDTO> findAllWhereJobHistoryIsNull();

    /**
     * Returns the number of mstEmployees available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstEmployees available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstEmployee.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstEmployeeDTO> findOne(Long id);

    /**
     * Delete the "id" mstEmployee.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstEmployee corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstEmployeeDTO> search(String query, Pageable pageable);
}
