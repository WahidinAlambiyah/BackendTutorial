package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.JobHistoryCriteria;
import com.mycompany.myapp.service.dto.JobHistoryDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.JobHistory}.
 */
public interface JobHistoryService {
    /**
     * Save a jobHistory.
     *
     * @param jobHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<JobHistoryDTO> save(JobHistoryDTO jobHistoryDTO);

    /**
     * Updates a jobHistory.
     *
     * @param jobHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<JobHistoryDTO> update(JobHistoryDTO jobHistoryDTO);

    /**
     * Partially updates a jobHistory.
     *
     * @param jobHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<JobHistoryDTO> partialUpdate(JobHistoryDTO jobHistoryDTO);
    /**
     * Find jobHistories by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<JobHistoryDTO> findByCriteria(JobHistoryCriteria criteria, Pageable pageable);

    /**
     * Find the count of jobHistories by criteria.
     * @param criteria filtering criteria
     * @return the count of jobHistories
     */
    public Mono<Long> countByCriteria(JobHistoryCriteria criteria);

    /**
     * Returns the number of jobHistories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of jobHistories available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" jobHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<JobHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" jobHistory.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the jobHistory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<JobHistoryDTO> search(String query, Pageable pageable);
}
