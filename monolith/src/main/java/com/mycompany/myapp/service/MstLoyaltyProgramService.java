package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstLoyaltyProgramCriteria;
import com.mycompany.myapp.service.dto.MstLoyaltyProgramDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstLoyaltyProgram}.
 */
public interface MstLoyaltyProgramService {
    /**
     * Save a mstLoyaltyProgram.
     *
     * @param mstLoyaltyProgramDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstLoyaltyProgramDTO> save(MstLoyaltyProgramDTO mstLoyaltyProgramDTO);

    /**
     * Updates a mstLoyaltyProgram.
     *
     * @param mstLoyaltyProgramDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstLoyaltyProgramDTO> update(MstLoyaltyProgramDTO mstLoyaltyProgramDTO);

    /**
     * Partially updates a mstLoyaltyProgram.
     *
     * @param mstLoyaltyProgramDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstLoyaltyProgramDTO> partialUpdate(MstLoyaltyProgramDTO mstLoyaltyProgramDTO);
    /**
     * Find mstLoyaltyPrograms by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstLoyaltyProgramDTO> findByCriteria(MstLoyaltyProgramCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstLoyaltyPrograms by criteria.
     * @param criteria filtering criteria
     * @return the count of mstLoyaltyPrograms
     */
    public Mono<Long> countByCriteria(MstLoyaltyProgramCriteria criteria);

    /**
     * Returns the number of mstLoyaltyPrograms available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstLoyaltyPrograms available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstLoyaltyProgram.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstLoyaltyProgramDTO> findOne(Long id);

    /**
     * Delete the "id" mstLoyaltyProgram.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstLoyaltyProgram corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstLoyaltyProgramDTO> search(String query, Pageable pageable);
}
