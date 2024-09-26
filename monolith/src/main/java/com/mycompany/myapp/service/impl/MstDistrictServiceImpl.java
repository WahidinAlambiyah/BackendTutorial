package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstDistrictCriteria;
import com.mycompany.myapp.repository.MstDistrictRepository;
import com.mycompany.myapp.repository.search.MstDistrictSearchRepository;
import com.mycompany.myapp.service.MstDistrictService;
import com.mycompany.myapp.service.dto.MstDistrictDTO;
import com.mycompany.myapp.service.mapper.MstDistrictMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstDistrict}.
 */
@Service
@Transactional
public class MstDistrictServiceImpl implements MstDistrictService {

    private static final Logger log = LoggerFactory.getLogger(MstDistrictServiceImpl.class);

    private final MstDistrictRepository mstDistrictRepository;

    private final MstDistrictMapper mstDistrictMapper;

    private final MstDistrictSearchRepository mstDistrictSearchRepository;

    public MstDistrictServiceImpl(
        MstDistrictRepository mstDistrictRepository,
        MstDistrictMapper mstDistrictMapper,
        MstDistrictSearchRepository mstDistrictSearchRepository
    ) {
        this.mstDistrictRepository = mstDistrictRepository;
        this.mstDistrictMapper = mstDistrictMapper;
        this.mstDistrictSearchRepository = mstDistrictSearchRepository;
    }

    @Override
    public Mono<MstDistrictDTO> save(MstDistrictDTO mstDistrictDTO) {
        log.debug("Request to save MstDistrict : {}", mstDistrictDTO);
        return mstDistrictRepository
            .save(mstDistrictMapper.toEntity(mstDistrictDTO))
            .flatMap(mstDistrictSearchRepository::save)
            .map(mstDistrictMapper::toDto);
    }

    @Override
    public Mono<MstDistrictDTO> update(MstDistrictDTO mstDistrictDTO) {
        log.debug("Request to update MstDistrict : {}", mstDistrictDTO);
        return mstDistrictRepository
            .save(mstDistrictMapper.toEntity(mstDistrictDTO))
            .flatMap(mstDistrictSearchRepository::save)
            .map(mstDistrictMapper::toDto);
    }

    @Override
    public Mono<MstDistrictDTO> partialUpdate(MstDistrictDTO mstDistrictDTO) {
        log.debug("Request to partially update MstDistrict : {}", mstDistrictDTO);

        return mstDistrictRepository
            .findById(mstDistrictDTO.getId())
            .map(existingMstDistrict -> {
                mstDistrictMapper.partialUpdate(existingMstDistrict, mstDistrictDTO);

                return existingMstDistrict;
            })
            .flatMap(mstDistrictRepository::save)
            .flatMap(savedMstDistrict -> {
                mstDistrictSearchRepository.save(savedMstDistrict);
                return Mono.just(savedMstDistrict);
            })
            .map(mstDistrictMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstDistrictDTO> findByCriteria(MstDistrictCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstDistricts by Criteria");
        return mstDistrictRepository.findByCriteria(criteria, pageable).map(mstDistrictMapper::toDto);
    }

    /**
     * Find the count of mstDistricts by criteria.
     * @param criteria filtering criteria
     * @return the count of mstDistricts
     */
    public Mono<Long> countByCriteria(MstDistrictCriteria criteria) {
        log.debug("Request to get the count of all MstDistricts by Criteria");
        return mstDistrictRepository.countByCriteria(criteria);
    }

    public Flux<MstDistrictDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mstDistrictRepository.findAllWithEagerRelationships(pageable).map(mstDistrictMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstDistrictRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstDistrictSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstDistrictDTO> findOne(Long id) {
        log.debug("Request to get MstDistrict : {}", id);
        return mstDistrictRepository.findOneWithEagerRelationships(id).map(mstDistrictMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstDistrict : {}", id);
        return mstDistrictRepository.deleteById(id).then(mstDistrictSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstDistrictDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstDistricts for query {}", query);
        return mstDistrictSearchRepository.search(query, pageable).map(mstDistrictMapper::toDto);
    }
}
