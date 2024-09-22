package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.ProvinceCriteria;
import com.mycompany.myapp.service.dto.ProvinceDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Province}.
 */
public interface ProvinceService {
    /**
     * Save a province.
     *
     * @param provinceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProvinceDTO> save(ProvinceDTO provinceDTO);

    /**
     * Updates a province.
     *
     * @param provinceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProvinceDTO> update(ProvinceDTO provinceDTO);

    /**
     * Partially updates a province.
     *
     * @param provinceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProvinceDTO> partialUpdate(ProvinceDTO provinceDTO);
    /**
     * Find provinces by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProvinceDTO> findByCriteria(ProvinceCriteria criteria, Pageable pageable);

    /**
     * Find the count of provinces by criteria.
     * @param criteria filtering criteria
     * @return the count of provinces
     */
    public Mono<Long> countByCriteria(ProvinceCriteria criteria);

    /**
     * Get all the provinces with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProvinceDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of provinces available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of provinces available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" province.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProvinceDTO> findOne(Long id);

    /**
     * Delete the "id" province.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the province corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProvinceDTO> search(String query, Pageable pageable);
}
