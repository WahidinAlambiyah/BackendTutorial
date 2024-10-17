package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstCustomerCriteria;
import com.mycompany.myapp.repository.MstCustomerRepository;
import com.mycompany.myapp.repository.search.MstCustomerSearchRepository;
import com.mycompany.myapp.service.MstCustomerService;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
import com.mycompany.myapp.service.mapper.MstCustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstCustomer}.
 */
@Service
@Transactional
public class MstCustomerServiceImpl implements MstCustomerService {

    private static final Logger log = LoggerFactory.getLogger(MstCustomerServiceImpl.class);

    private final MstCustomerRepository mstCustomerRepository;

    private final MstCustomerMapper mstCustomerMapper;

    private final MstCustomerSearchRepository mstCustomerSearchRepository;

    public MstCustomerServiceImpl(
        MstCustomerRepository mstCustomerRepository,
        MstCustomerMapper mstCustomerMapper,
        MstCustomerSearchRepository mstCustomerSearchRepository
    ) {
        this.mstCustomerRepository = mstCustomerRepository;
        this.mstCustomerMapper = mstCustomerMapper;
        this.mstCustomerSearchRepository = mstCustomerSearchRepository;
    }

    @Override
    public Mono<MstCustomerDTO> save(MstCustomerDTO mstCustomerDTO) {
        log.debug("Request to save MstCustomer : {}", mstCustomerDTO);
        return mstCustomerRepository
            .save(mstCustomerMapper.toEntity(mstCustomerDTO))
            .flatMap(mstCustomerSearchRepository::save)
            .map(mstCustomerMapper::toDto);
    }

    @Override
    public Mono<MstCustomerDTO> update(MstCustomerDTO mstCustomerDTO) {
        log.debug("Request to update MstCustomer : {}", mstCustomerDTO);
        return mstCustomerRepository
            .save(mstCustomerMapper.toEntity(mstCustomerDTO))
            .flatMap(mstCustomerSearchRepository::save)
            .map(mstCustomerMapper::toDto);
    }

    @Override
    public Mono<MstCustomerDTO> partialUpdate(MstCustomerDTO mstCustomerDTO) {
        log.debug("Request to partially update MstCustomer : {}", mstCustomerDTO);

        return mstCustomerRepository
            .findById(mstCustomerDTO.getId())
            .map(existingMstCustomer -> {
                mstCustomerMapper.partialUpdate(existingMstCustomer, mstCustomerDTO);

                return existingMstCustomer;
            })
            .flatMap(mstCustomerRepository::save)
            .flatMap(savedMstCustomer -> {
                mstCustomerSearchRepository.save(savedMstCustomer);
                return Mono.just(savedMstCustomer);
            })
            .map(mstCustomerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstCustomerDTO> findByCriteria(MstCustomerCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstCustomers by Criteria");
        return mstCustomerRepository.findByCriteria(criteria, pageable).map(mstCustomerMapper::toDto);
    }

    /**
     * Find the count of mstCustomers by criteria.
     * @param criteria filtering criteria
     * @return the count of mstCustomers
     */
    public Mono<Long> countByCriteria(MstCustomerCriteria criteria) {
        log.debug("Request to get the count of all MstCustomers by Criteria");
        return mstCustomerRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstCustomerRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstCustomerSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstCustomerDTO> findOne(Long id) {
        log.debug("Request to get MstCustomer : {}", id);
        return mstCustomerRepository.findById(id).map(mstCustomerMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstCustomer : {}", id);
        return mstCustomerRepository.deleteById(id).then(mstCustomerSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstCustomerDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstCustomers for query {}", query);
        return mstCustomerSearchRepository.search(query, pageable).map(mstCustomerMapper::toDto);
    }
}
