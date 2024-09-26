package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstSubDistrictCriteria;
import com.mycompany.myapp.repository.MstSubDistrictRepository;
import com.mycompany.myapp.repository.search.MstSubDistrictSearchRepository;
import com.mycompany.myapp.service.MstSubDistrictService;
import com.mycompany.myapp.service.dto.MstSubDistrictDTO;
import com.mycompany.myapp.service.mapper.MstSubDistrictMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstSubDistrict}.
 */
@Service
@Transactional
public class MstSubDistrictServiceImpl implements MstSubDistrictService {

    private static final Logger log = LoggerFactory.getLogger(MstSubDistrictServiceImpl.class);

    private final MstSubDistrictRepository mstSubDistrictRepository;

    private final MstSubDistrictMapper mstSubDistrictMapper;

    private final MstSubDistrictSearchRepository mstSubDistrictSearchRepository;

    public MstSubDistrictServiceImpl(
        MstSubDistrictRepository mstSubDistrictRepository,
        MstSubDistrictMapper mstSubDistrictMapper,
        MstSubDistrictSearchRepository mstSubDistrictSearchRepository
    ) {
        this.mstSubDistrictRepository = mstSubDistrictRepository;
        this.mstSubDistrictMapper = mstSubDistrictMapper;
        this.mstSubDistrictSearchRepository = mstSubDistrictSearchRepository;
    }

    @Override
    public Mono<MstSubDistrictDTO> save(MstSubDistrictDTO mstSubDistrictDTO) {
        log.debug("Request to save MstSubDistrict : {}", mstSubDistrictDTO);
        return mstSubDistrictRepository
            .save(mstSubDistrictMapper.toEntity(mstSubDistrictDTO))
            .flatMap(mstSubDistrictSearchRepository::save)
            .map(mstSubDistrictMapper::toDto);
    }

    @Override
    public Mono<MstSubDistrictDTO> update(MstSubDistrictDTO mstSubDistrictDTO) {
        log.debug("Request to update MstSubDistrict : {}", mstSubDistrictDTO);
        return mstSubDistrictRepository
            .save(mstSubDistrictMapper.toEntity(mstSubDistrictDTO))
            .flatMap(mstSubDistrictSearchRepository::save)
            .map(mstSubDistrictMapper::toDto);
    }

    @Override
    public Mono<MstSubDistrictDTO> partialUpdate(MstSubDistrictDTO mstSubDistrictDTO) {
        log.debug("Request to partially update MstSubDistrict : {}", mstSubDistrictDTO);

        return mstSubDistrictRepository
            .findById(mstSubDistrictDTO.getId())
            .map(existingMstSubDistrict -> {
                mstSubDistrictMapper.partialUpdate(existingMstSubDistrict, mstSubDistrictDTO);

                return existingMstSubDistrict;
            })
            .flatMap(mstSubDistrictRepository::save)
            .flatMap(savedMstSubDistrict -> {
                mstSubDistrictSearchRepository.save(savedMstSubDistrict);
                return Mono.just(savedMstSubDistrict);
            })
            .map(mstSubDistrictMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstSubDistrictDTO> findByCriteria(MstSubDistrictCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstSubDistricts by Criteria");
        return mstSubDistrictRepository.findByCriteria(criteria, pageable).map(mstSubDistrictMapper::toDto);
    }

    /**
     * Find the count of mstSubDistricts by criteria.
     * @param criteria filtering criteria
     * @return the count of mstSubDistricts
     */
    public Mono<Long> countByCriteria(MstSubDistrictCriteria criteria) {
        log.debug("Request to get the count of all MstSubDistricts by Criteria");
        return mstSubDistrictRepository.countByCriteria(criteria);
    }

    public Flux<MstSubDistrictDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mstSubDistrictRepository.findAllWithEagerRelationships(pageable).map(mstSubDistrictMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstSubDistrictRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstSubDistrictSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstSubDistrictDTO> findOne(Long id) {
        log.debug("Request to get MstSubDistrict : {}", id);
        return mstSubDistrictRepository.findOneWithEagerRelationships(id).map(mstSubDistrictMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstSubDistrict : {}", id);
        return mstSubDistrictRepository.deleteById(id).then(mstSubDistrictSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstSubDistrictDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstSubDistricts for query {}", query);
        return mstSubDistrictSearchRepository.search(query, pageable).map(mstSubDistrictMapper::toDto);
    }
}
