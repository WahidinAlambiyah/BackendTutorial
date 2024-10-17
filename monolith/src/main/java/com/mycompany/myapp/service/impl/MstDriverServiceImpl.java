package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstDriverCriteria;
import com.mycompany.myapp.repository.MstDriverRepository;
import com.mycompany.myapp.repository.search.MstDriverSearchRepository;
import com.mycompany.myapp.service.MstDriverService;
import com.mycompany.myapp.service.dto.MstDriverDTO;
import com.mycompany.myapp.service.mapper.MstDriverMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstDriver}.
 */
@Service
@Transactional
public class MstDriverServiceImpl implements MstDriverService {

    private static final Logger log = LoggerFactory.getLogger(MstDriverServiceImpl.class);

    private final MstDriverRepository mstDriverRepository;

    private final MstDriverMapper mstDriverMapper;

    private final MstDriverSearchRepository mstDriverSearchRepository;

    public MstDriverServiceImpl(
        MstDriverRepository mstDriverRepository,
        MstDriverMapper mstDriverMapper,
        MstDriverSearchRepository mstDriverSearchRepository
    ) {
        this.mstDriverRepository = mstDriverRepository;
        this.mstDriverMapper = mstDriverMapper;
        this.mstDriverSearchRepository = mstDriverSearchRepository;
    }

    @Override
    public Mono<MstDriverDTO> save(MstDriverDTO mstDriverDTO) {
        log.debug("Request to save MstDriver : {}", mstDriverDTO);
        return mstDriverRepository
            .save(mstDriverMapper.toEntity(mstDriverDTO))
            .flatMap(mstDriverSearchRepository::save)
            .map(mstDriverMapper::toDto);
    }

    @Override
    public Mono<MstDriverDTO> update(MstDriverDTO mstDriverDTO) {
        log.debug("Request to update MstDriver : {}", mstDriverDTO);
        return mstDriverRepository
            .save(mstDriverMapper.toEntity(mstDriverDTO))
            .flatMap(mstDriverSearchRepository::save)
            .map(mstDriverMapper::toDto);
    }

    @Override
    public Mono<MstDriverDTO> partialUpdate(MstDriverDTO mstDriverDTO) {
        log.debug("Request to partially update MstDriver : {}", mstDriverDTO);

        return mstDriverRepository
            .findById(mstDriverDTO.getId())
            .map(existingMstDriver -> {
                mstDriverMapper.partialUpdate(existingMstDriver, mstDriverDTO);

                return existingMstDriver;
            })
            .flatMap(mstDriverRepository::save)
            .flatMap(savedMstDriver -> {
                mstDriverSearchRepository.save(savedMstDriver);
                return Mono.just(savedMstDriver);
            })
            .map(mstDriverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstDriverDTO> findByCriteria(MstDriverCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstDrivers by Criteria");
        return mstDriverRepository.findByCriteria(criteria, pageable).map(mstDriverMapper::toDto);
    }

    /**
     * Find the count of mstDrivers by criteria.
     * @param criteria filtering criteria
     * @return the count of mstDrivers
     */
    public Mono<Long> countByCriteria(MstDriverCriteria criteria) {
        log.debug("Request to get the count of all MstDrivers by Criteria");
        return mstDriverRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstDriverRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstDriverSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstDriverDTO> findOne(Long id) {
        log.debug("Request to get MstDriver : {}", id);
        return mstDriverRepository.findById(id).map(mstDriverMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstDriver : {}", id);
        return mstDriverRepository.deleteById(id).then(mstDriverSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstDriverDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstDrivers for query {}", query);
        return mstDriverSearchRepository.search(query, pageable).map(mstDriverMapper::toDto);
    }
}
