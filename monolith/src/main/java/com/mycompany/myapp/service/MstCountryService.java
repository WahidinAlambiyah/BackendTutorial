package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstCountryCriteria;
import com.mycompany.myapp.service.dto.MstCountryDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstCountry}.
 */
public interface MstCountryService {
    /**
     * Save a mstCountry.
     *
     * @param mstCountryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstCountryDTO> save(MstCountryDTO mstCountryDTO);

    /**
     * Updates a mstCountry.
     *
     * @param mstCountryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstCountryDTO> update(MstCountryDTO mstCountryDTO);

    /**
     * Partially updates a mstCountry.
     *
     * @param mstCountryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstCountryDTO> partialUpdate(MstCountryDTO mstCountryDTO);
    /**
     * Find mstCountries by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCountryDTO> findByCriteria(MstCountryCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstCountries by criteria.
     * @param criteria filtering criteria
     * @return the count of mstCountries
     */
    public Mono<Long> countByCriteria(MstCountryCriteria criteria);

    /**
     * Get all the mstCountries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCountryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of mstCountries available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstCountries available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstCountry.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstCountryDTO> findOne(Long id);

    /**
     * Delete the "id" mstCountry.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstCountry corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCountryDTO> search(String query, Pageable pageable);
}
