package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxTestimonialCriteria;
import com.mycompany.myapp.service.dto.TrxTestimonialDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxTestimonial}.
 */
public interface TrxTestimonialService {
    /**
     * Save a trxTestimonial.
     *
     * @param trxTestimonialDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxTestimonialDTO> save(TrxTestimonialDTO trxTestimonialDTO);

    /**
     * Updates a trxTestimonial.
     *
     * @param trxTestimonialDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxTestimonialDTO> update(TrxTestimonialDTO trxTestimonialDTO);

    /**
     * Partially updates a trxTestimonial.
     *
     * @param trxTestimonialDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxTestimonialDTO> partialUpdate(TrxTestimonialDTO trxTestimonialDTO);
    /**
     * Find trxTestimonials by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxTestimonialDTO> findByCriteria(TrxTestimonialCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxTestimonials by criteria.
     * @param criteria filtering criteria
     * @return the count of trxTestimonials
     */
    public Mono<Long> countByCriteria(TrxTestimonialCriteria criteria);

    /**
     * Returns the number of trxTestimonials available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxTestimonials available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxTestimonial.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxTestimonialDTO> findOne(Long id);

    /**
     * Delete the "id" trxTestimonial.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxTestimonial corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxTestimonialDTO> search(String query, Pageable pageable);
}
