package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxTournamentCriteria;
import com.mycompany.myapp.service.dto.TrxTournamentDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxTournament}.
 */
public interface TrxTournamentService {
    /**
     * Save a trxTournament.
     *
     * @param trxTournamentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxTournamentDTO> save(TrxTournamentDTO trxTournamentDTO);

    /**
     * Updates a trxTournament.
     *
     * @param trxTournamentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxTournamentDTO> update(TrxTournamentDTO trxTournamentDTO);

    /**
     * Partially updates a trxTournament.
     *
     * @param trxTournamentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxTournamentDTO> partialUpdate(TrxTournamentDTO trxTournamentDTO);
    /**
     * Find trxTournaments by criteria.
     *
     * @return the list of entities.
     */
    Flux<TrxTournamentDTO> findByCriteria(TrxTournamentCriteria criteria);

    /**
     * Find the count of trxTournaments by criteria.
     * @param criteria filtering criteria
     * @return the count of trxTournaments
     */
    public Mono<Long> countByCriteria(TrxTournamentCriteria criteria);

    /**
     * Get all the trxTournaments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxTournamentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of trxTournaments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxTournaments available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxTournament.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxTournamentDTO> findOne(Long id);

    /**
     * Delete the "id" trxTournament.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxTournament corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<TrxTournamentDTO> search(String query);
}
