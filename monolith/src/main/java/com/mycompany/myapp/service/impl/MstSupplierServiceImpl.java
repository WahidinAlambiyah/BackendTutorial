package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstSupplierCriteria;
import com.mycompany.myapp.repository.MstSupplierRepository;
import com.mycompany.myapp.repository.search.MstSupplierSearchRepository;
import com.mycompany.myapp.service.MstSupplierService;
import com.mycompany.myapp.service.dto.MstSupplierDTO;
import com.mycompany.myapp.service.mapper.MstSupplierMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstSupplier}.
 */
@Service
@Transactional
public class MstSupplierServiceImpl implements MstSupplierService {

    private static final Logger log = LoggerFactory.getLogger(MstSupplierServiceImpl.class);

    private final MstSupplierRepository mstSupplierRepository;

    private final MstSupplierMapper mstSupplierMapper;

    private final MstSupplierSearchRepository mstSupplierSearchRepository;

    public MstSupplierServiceImpl(
        MstSupplierRepository mstSupplierRepository,
        MstSupplierMapper mstSupplierMapper,
        MstSupplierSearchRepository mstSupplierSearchRepository
    ) {
        this.mstSupplierRepository = mstSupplierRepository;
        this.mstSupplierMapper = mstSupplierMapper;
        this.mstSupplierSearchRepository = mstSupplierSearchRepository;
    }

    @Override
    public Mono<MstSupplierDTO> save(MstSupplierDTO mstSupplierDTO) {
        log.debug("Request to save MstSupplier : {}", mstSupplierDTO);
        return mstSupplierRepository
            .save(mstSupplierMapper.toEntity(mstSupplierDTO))
            .flatMap(mstSupplierSearchRepository::save)
            .map(mstSupplierMapper::toDto);
    }

    @Override
    public Mono<MstSupplierDTO> update(MstSupplierDTO mstSupplierDTO) {
        log.debug("Request to update MstSupplier : {}", mstSupplierDTO);
        return mstSupplierRepository
            .save(mstSupplierMapper.toEntity(mstSupplierDTO))
            .flatMap(mstSupplierSearchRepository::save)
            .map(mstSupplierMapper::toDto);
    }

    @Override
    public Mono<MstSupplierDTO> partialUpdate(MstSupplierDTO mstSupplierDTO) {
        log.debug("Request to partially update MstSupplier : {}", mstSupplierDTO);

        return mstSupplierRepository
            .findById(mstSupplierDTO.getId())
            .map(existingMstSupplier -> {
                mstSupplierMapper.partialUpdate(existingMstSupplier, mstSupplierDTO);

                return existingMstSupplier;
            })
            .flatMap(mstSupplierRepository::save)
            .flatMap(savedMstSupplier -> {
                mstSupplierSearchRepository.save(savedMstSupplier);
                return Mono.just(savedMstSupplier);
            })
            .map(mstSupplierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstSupplierDTO> findByCriteria(MstSupplierCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstSuppliers by Criteria");
        return mstSupplierRepository.findByCriteria(criteria, pageable).map(mstSupplierMapper::toDto);
    }

    /**
     * Find the count of mstSuppliers by criteria.
     * @param criteria filtering criteria
     * @return the count of mstSuppliers
     */
    public Mono<Long> countByCriteria(MstSupplierCriteria criteria) {
        log.debug("Request to get the count of all MstSuppliers by Criteria");
        return mstSupplierRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstSupplierRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstSupplierSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstSupplierDTO> findOne(Long id) {
        log.debug("Request to get MstSupplier : {}", id);
        return mstSupplierRepository.findById(id).map(mstSupplierMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstSupplier : {}", id);
        return mstSupplierRepository.deleteById(id).then(mstSupplierSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstSupplierDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstSuppliers for query {}", query);
        return mstSupplierSearchRepository.search(query, pageable).map(mstSupplierMapper::toDto);
    }
}
