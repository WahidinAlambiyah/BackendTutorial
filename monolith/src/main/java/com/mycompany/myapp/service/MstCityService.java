package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstCityCriteria;
import com.mycompany.myapp.service.dto.MstCityDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstCity}.
 */
public interface MstCityService {
    /**
     * Save a mstCity.
     *
     * @param mstCityDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstCityDTO> save(MstCityDTO mstCityDTO);

    /**
     * Updates a mstCity.
     *
     * @param mstCityDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstCityDTO> update(MstCityDTO mstCityDTO);

    /**
     * Partially updates a mstCity.
     *
     * @param mstCityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstCityDTO> partialUpdate(MstCityDTO mstCityDTO);
    /**
     * Find mstCities by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCityDTO> findByCriteria(MstCityCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstCities by criteria.
     * @param criteria filtering criteria
     * @return the count of mstCities
     */
    public Mono<Long> countByCriteria(MstCityCriteria criteria);

    /**
     * Get all the mstCities with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCityDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of mstCities available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstCities available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstCity.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstCityDTO> findOne(Long id);

    /**
     * Delete the "id" mstCity.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstCity corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCityDTO> search(String query, Pageable pageable);
}
