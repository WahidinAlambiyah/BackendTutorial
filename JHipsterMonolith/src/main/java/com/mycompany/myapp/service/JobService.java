package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.JobCriteria;
import com.mycompany.myapp.service.dto.JobDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Job}.
 */
public interface JobService {
    /**
     * Save a job.
     *
     * @param jobDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<JobDTO> save(JobDTO jobDTO);

    /**
     * Updates a job.
     *
     * @param jobDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<JobDTO> update(JobDTO jobDTO);

    /**
     * Partially updates a job.
     *
     * @param jobDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<JobDTO> partialUpdate(JobDTO jobDTO);
    /**
     * Find jobs by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<JobDTO> findByCriteria(JobCriteria criteria, Pageable pageable);

    /**
     * Find the count of jobs by criteria.
     * @param criteria filtering criteria
     * @return the count of jobs
     */
    public Mono<Long> countByCriteria(JobCriteria criteria);

    /**
     * Get all the JobDTO where JobHistory is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<JobDTO> findAllWhereJobHistoryIsNull();

    /**
     * Get all the jobs with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<JobDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of jobs available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of jobs available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" job.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<JobDTO> findOne(Long id);

    /**
     * Delete the "id" job.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the job corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<JobDTO> search(String query, Pageable pageable);
}
