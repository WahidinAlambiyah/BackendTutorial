package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PostalCode;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.PostalCode}.
 */
public interface PostalCodeService {
    /**
     * Save a postalCode.
     *
     * @param postalCode the entity to save.
     * @return the persisted entity.
     */
    Mono<PostalCode> save(PostalCode postalCode);

    /**
     * Updates a postalCode.
     *
     * @param postalCode the entity to update.
     * @return the persisted entity.
     */
    Mono<PostalCode> update(PostalCode postalCode);

    /**
     * Partially updates a postalCode.
     *
     * @param postalCode the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PostalCode> partialUpdate(PostalCode postalCode);

    /**
     * Get all the postalCodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PostalCode> findAll(Pageable pageable);

    /**
     * Get all the postalCodes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PostalCode> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of postalCodes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of postalCodes available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" postalCode.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PostalCode> findOne(Long id);

    /**
     * Delete the "id" postalCode.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the postalCode corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PostalCode> search(String query, Pageable pageable);
}
