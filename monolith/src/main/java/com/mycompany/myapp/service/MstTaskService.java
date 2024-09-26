package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstTaskCriteria;
import com.mycompany.myapp.service.dto.MstTaskDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstTask}.
 */
public interface MstTaskService {
    /**
     * Save a mstTask.
     *
     * @param mstTaskDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstTaskDTO> save(MstTaskDTO mstTaskDTO);

    /**
     * Updates a mstTask.
     *
     * @param mstTaskDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstTaskDTO> update(MstTaskDTO mstTaskDTO);

    /**
     * Partially updates a mstTask.
     *
     * @param mstTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstTaskDTO> partialUpdate(MstTaskDTO mstTaskDTO);
    /**
     * Find mstTasks by criteria.
     *
     * @return the list of entities.
     */
    Flux<MstTaskDTO> findByCriteria(MstTaskCriteria criteria);

    /**
     * Find the count of mstTasks by criteria.
     * @param criteria filtering criteria
     * @return the count of mstTasks
     */
    public Mono<Long> countByCriteria(MstTaskCriteria criteria);

    /**
     * Returns the number of mstTasks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstTasks available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstTask.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstTaskDTO> findOne(Long id);

    /**
     * Delete the "id" mstTask.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstTask corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<MstTaskDTO> search(String query);
}
