package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstServiceCriteria;
import com.mycompany.myapp.repository.MstServiceRepository;
import com.mycompany.myapp.repository.search.MstServiceSearchRepository;
import com.mycompany.myapp.service.MstServiceService;
import com.mycompany.myapp.service.dto.MstServiceDTO;
import com.mycompany.myapp.service.mapper.MstServiceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstService}.
 */
@Service
@Transactional
public class MstServiceServiceImpl implements MstServiceService {

    private static final Logger log = LoggerFactory.getLogger(MstServiceServiceImpl.class);

    private final MstServiceRepository mstServiceRepository;

    private final MstServiceMapper mstServiceMapper;

    private final MstServiceSearchRepository mstServiceSearchRepository;

    public MstServiceServiceImpl(
        MstServiceRepository mstServiceRepository,
        MstServiceMapper mstServiceMapper,
        MstServiceSearchRepository mstServiceSearchRepository
    ) {
        this.mstServiceRepository = mstServiceRepository;
        this.mstServiceMapper = mstServiceMapper;
        this.mstServiceSearchRepository = mstServiceSearchRepository;
    }

    @Override
    public Mono<MstServiceDTO> save(MstServiceDTO mstServiceDTO) {
        log.debug("Request to save MstService : {}", mstServiceDTO);
        return mstServiceRepository
            .save(mstServiceMapper.toEntity(mstServiceDTO))
            .flatMap(mstServiceSearchRepository::save)
            .map(mstServiceMapper::toDto);
    }

    @Override
    public Mono<MstServiceDTO> update(MstServiceDTO mstServiceDTO) {
        log.debug("Request to update MstService : {}", mstServiceDTO);
        return mstServiceRepository
            .save(mstServiceMapper.toEntity(mstServiceDTO))
            .flatMap(mstServiceSearchRepository::save)
            .map(mstServiceMapper::toDto);
    }

    @Override
    public Mono<MstServiceDTO> partialUpdate(MstServiceDTO mstServiceDTO) {
        log.debug("Request to partially update MstService : {}", mstServiceDTO);

        return mstServiceRepository
            .findById(mstServiceDTO.getId())
            .map(existingMstService -> {
                mstServiceMapper.partialUpdate(existingMstService, mstServiceDTO);

                return existingMstService;
            })
            .flatMap(mstServiceRepository::save)
            .flatMap(savedMstService -> {
                mstServiceSearchRepository.save(savedMstService);
                return Mono.just(savedMstService);
            })
            .map(mstServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstServiceDTO> findByCriteria(MstServiceCriteria criteria) {
        log.debug("Request to get all MstServices by Criteria");
        return mstServiceRepository.findByCriteria(criteria, null).map(mstServiceMapper::toDto);
    }

    /**
     * Find the count of mstServices by criteria.
     * @param criteria filtering criteria
     * @return the count of mstServices
     */
    public Mono<Long> countByCriteria(MstServiceCriteria criteria) {
        log.debug("Request to get the count of all MstServices by Criteria");
        return mstServiceRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstServiceRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstServiceSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstServiceDTO> findOne(Long id) {
        log.debug("Request to get MstService : {}", id);
        return mstServiceRepository.findById(id).map(mstServiceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstService : {}", id);
        return mstServiceRepository.deleteById(id).then(mstServiceSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstServiceDTO> search(String query) {
        log.debug("Request to search MstServices for query {}", query);
        try {
            return mstServiceSearchRepository.search(query).map(mstServiceMapper::toDto);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
