package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.criteria.MstCustomerCriteria;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.MstCustomer}.
 */
public interface MstCustomerService {
    /**
     * Save a mstCustomer.
     *
     * @param mstCustomerDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MstCustomerDTO> save(MstCustomerDTO mstCustomerDTO);

    /**
     * Updates a mstCustomer.
     *
     * @param mstCustomerDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MstCustomerDTO> update(MstCustomerDTO mstCustomerDTO);

    /**
     * Partially updates a mstCustomer.
     *
     * @param mstCustomerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MstCustomerDTO> partialUpdate(MstCustomerDTO mstCustomerDTO);
    /**
     * Find mstCustomers by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCustomerDTO> findByCriteria(MstCustomerCriteria criteria, Pageable pageable);

    /**
     * Find the count of mstCustomers by criteria.
     * @param criteria filtering criteria
     * @return the count of mstCustomers
     */
    public Mono<Long> countByCriteria(MstCustomerCriteria criteria);

    /**
     * Returns the number of mstCustomers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of mstCustomers available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" mstCustomer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MstCustomerDTO> findOne(Long id);

    /**
     * Delete the "id" mstCustomer.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the mstCustomer corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MstCustomerDTO> search(String query, Pageable pageable);
}
