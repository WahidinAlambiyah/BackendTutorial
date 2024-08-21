package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.CountryCriteria;
import com.mycompany.myapp.service.dto.CountryDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Country}.
 */
public interface CountryService {
    /**
     * Save a country.
     *
     * @param countryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CountryDTO> save(CountryDTO countryDTO);

    /**
     * Updates a country.
     *
     * @param countryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CountryDTO> update(CountryDTO countryDTO);

    /**
     * Partially updates a country.
     *
     * @param countryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CountryDTO> partialUpdate(CountryDTO countryDTO);
    /**
     * Find countries by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CountryDTO> findByCriteria(CountryCriteria criteria, Pageable pageable);

    /**
     * Find the count of countries by criteria.
     * @param criteria filtering criteria
     * @return the count of countries
     */
    public Mono<Long> countByCriteria(CountryCriteria criteria);

    /**
     * Get all the countries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CountryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of countries available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of countries available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" country.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CountryDTO> findOne(Long id);

    /**
     * Delete the "id" country.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the country corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CountryDTO> search(String query, Pageable pageable);
}
