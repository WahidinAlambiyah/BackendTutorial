package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstJobCriteria;
import com.mycompany.myapp.service.dto.MstJobDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstJob}.
 */
public interface MstJobService {
    /**
     * Save a mstJob.
     *
     * @param mstJobDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstJobDTO> save(MstJobDTO mstJobDTO);

    /**
     * Updates a mstJob.
     *
     * @param mstJobDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstJobDTO> update(MstJobDTO mstJobDTO);

    /**
     * Partially updates a mstJob.
     *
     * @param mstJobDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstJobDTO> partialUpdate(MstJobDTO mstJobDTO);
    /**
     * Find mstJobs by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstJobDTO> findByCriteria(MstJobCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstJobs by criteria.
     * @param criteria filtering criteria
     * @return the count of mstJobs
     */
    public Mono<Long> countByCriteria(MstJobCriteria criteria);

    /**
     * Get all the MstJobDTO where JobHistory is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<MstJobDTO> findAllWhereJobHistoryIsNull();

    /**
     * Get all the mstJobs with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstJobDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of mstJobs available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstJobs available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstJob.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstJobDTO> findOne(Long id);

    /**
     * Delete the "id" mstJob.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstJob corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstJobDTO> search(String query, Pageable pageable);
}
