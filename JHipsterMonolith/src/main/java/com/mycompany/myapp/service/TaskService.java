package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TaskCriteria;
import com.mycompany.myapp.service.dto.TaskDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Task}.
 */
public interface TaskService {
    /**
     * Save a task.
     *
     * @param taskDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TaskDTO> save(TaskDTO taskDTO);

    /**
     * Updates a task.
     *
     * @param taskDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TaskDTO> update(TaskDTO taskDTO);

    /**
     * Partially updates a task.
     *
     * @param taskDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TaskDTO> partialUpdate(TaskDTO taskDTO);
    /**
     * Find tasks by criteria.
     *
     * @return the list of entities.
     */
    Flux<TaskDTO> findByCriteria(TaskCriteria criteria);

    /**
     * Find the count of tasks by criteria.
     * @param criteria filtering criteria
     * @return the count of tasks
     */
    public Mono<Long> countByCriteria(TaskCriteria criteria);

    /**
     * Returns the number of tasks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of tasks available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" task.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TaskDTO> findOne(Long id);

    /**
     * Delete the "id" task.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the task corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<TaskDTO> search(String query);
}
