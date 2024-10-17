package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxStockAlertCriteria;
import com.mycompany.myapp.service.dto.TrxStockAlertDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxStockAlert}.
 */
public interface TrxStockAlertService {
    /**
     * Save a trxStockAlert.
     *
     * @param trxStockAlertDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxStockAlertDTO> save(TrxStockAlertDTO trxStockAlertDTO);

    /**
     * Updates a trxStockAlert.
     *
     * @param trxStockAlertDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxStockAlertDTO> update(TrxStockAlertDTO trxStockAlertDTO);

    /**
     * Partially updates a trxStockAlert.
     *
     * @param trxStockAlertDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxStockAlertDTO> partialUpdate(TrxStockAlertDTO trxStockAlertDTO);
    /**
     * Find trxStockAlerts by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxStockAlertDTO> findByCriteria(TrxStockAlertCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxStockAlerts by criteria.
     * @param criteria filtering criteria
     * @return the count of trxStockAlerts
     */
    public Mono<Long> countByCriteria(TrxStockAlertCriteria criteria);

    /**
     * Returns the number of trxStockAlerts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxStockAlerts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxStockAlert.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxStockAlertDTO> findOne(Long id);

    /**
     * Delete the "id" trxStockAlert.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxStockAlert corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxStockAlertDTO> search(String query, Pageable pageable);
}
