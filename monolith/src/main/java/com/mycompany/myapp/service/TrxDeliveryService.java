package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxDeliveryCriteria;
import com.mycompany.myapp.service.dto.TrxDeliveryDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxDelivery}.
 */
public interface TrxDeliveryService {
    /**
     * Save a trxDelivery.
     *
     * @param trxDeliveryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxDeliveryDTO> save(TrxDeliveryDTO trxDeliveryDTO);

    /**
     * Updates a trxDelivery.
     *
     * @param trxDeliveryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxDeliveryDTO> update(TrxDeliveryDTO trxDeliveryDTO);

    /**
     * Partially updates a trxDelivery.
     *
     * @param trxDeliveryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxDeliveryDTO> partialUpdate(TrxDeliveryDTO trxDeliveryDTO);
    /**
     * Find trxDeliveries by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxDeliveryDTO> findByCriteria(TrxDeliveryCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxDeliveries by criteria.
     * @param criteria filtering criteria
     * @return the count of trxDeliveries
     */
    public Mono<Long> countByCriteria(TrxDeliveryCriteria criteria);

    /**
     * Returns the number of trxDeliveries available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxDeliveries available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxDelivery.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxDeliveryDTO> findOne(Long id);

    /**
     * Delete the "id" trxDelivery.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxDelivery corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxDeliveryDTO> search(String query, Pageable pageable);
}
