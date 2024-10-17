package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.TrxNotificationCriteria;
import com.mycompany.myapp.service.dto.TrxNotificationDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TrxNotification}.
 */
public interface TrxNotificationService {
    /**
     * Save a trxNotification.
     *
     * @param trxNotificationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TrxNotificationDTO> save(TrxNotificationDTO trxNotificationDTO);

    /**
     * Updates a trxNotification.
     *
     * @param trxNotificationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TrxNotificationDTO> update(TrxNotificationDTO trxNotificationDTO);

    /**
     * Partially updates a trxNotification.
     *
     * @param trxNotificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TrxNotificationDTO> partialUpdate(TrxNotificationDTO trxNotificationDTO);
    /**
     * Find trxNotifications by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxNotificationDTO> findByCriteria(TrxNotificationCriteria criteria, Pageable pageable);

    /**
     * Find the count of trxNotifications by criteria.
     * @param criteria filtering criteria
     * @return the count of trxNotifications
     */
    public Mono<Long> countByCriteria(TrxNotificationCriteria criteria);

    /**
     * Returns the number of trxNotifications available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of trxNotifications available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" trxNotification.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TrxNotificationDTO> findOne(Long id);

    /**
     * Delete the "id" trxNotification.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the trxNotification corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TrxNotificationDTO> search(String query, Pageable pageable);
}
