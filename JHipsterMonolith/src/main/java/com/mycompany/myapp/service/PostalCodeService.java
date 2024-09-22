package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.PostalCodeCriteria;
import com.mycompany.myapp.service.dto.PostalCodeDTO;
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
     * @param postalCodeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PostalCodeDTO> save(PostalCodeDTO postalCodeDTO);

    /**
     * Updates a postalCode.
     *
     * @param postalCodeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PostalCodeDTO> update(PostalCodeDTO postalCodeDTO);

    /**
     * Partially updates a postalCode.
     *
     * @param postalCodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PostalCodeDTO> partialUpdate(PostalCodeDTO postalCodeDTO);
    /**
     * Find postalCodes by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PostalCodeDTO> findByCriteria(PostalCodeCriteria criteria, Pageable pageable);

    /**
     * Find the count of postalCodes by criteria.
     * @param criteria filtering criteria
     * @return the count of postalCodes
     */
    public Mono<Long> countByCriteria(PostalCodeCriteria criteria);

    /**
     * Get all the postalCodes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PostalCodeDTO> findAllWithEagerRelationships(Pageable pageable);

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
    Mono<PostalCodeDTO> findOne(Long id);

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
    Flux<PostalCodeDTO> search(String query, Pageable pageable);
}
