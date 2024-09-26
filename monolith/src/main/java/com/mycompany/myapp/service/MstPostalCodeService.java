package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstPostalCodeCriteria;
import com.mycompany.myapp.service.dto.MstPostalCodeDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstPostalCode}.
 */
public interface MstPostalCodeService {
    /**
     * Save a mstPostalCode.
     *
     * @param mstPostalCodeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstPostalCodeDTO> save(MstPostalCodeDTO mstPostalCodeDTO);

    /**
     * Updates a mstPostalCode.
     *
     * @param mstPostalCodeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstPostalCodeDTO> update(MstPostalCodeDTO mstPostalCodeDTO);

    /**
     * Partially updates a mstPostalCode.
     *
     * @param mstPostalCodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstPostalCodeDTO> partialUpdate(MstPostalCodeDTO mstPostalCodeDTO);
    /**
     * Find mstPostalCodes by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstPostalCodeDTO> findByCriteria(MstPostalCodeCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstPostalCodes by criteria.
     * @param criteria filtering criteria
     * @return the count of mstPostalCodes
     */
    public Mono<Long> countByCriteria(MstPostalCodeCriteria criteria);

    /**
     * Get all the mstPostalCodes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstPostalCodeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of mstPostalCodes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstPostalCodes available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstPostalCode.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstPostalCodeDTO> findOne(Long id);

    /**
     * Delete the "id" mstPostalCode.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstPostalCode corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstPostalCodeDTO> search(String query, Pageable pageable);
}
