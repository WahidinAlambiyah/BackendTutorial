package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstRegionCriteria;
import com.mycompany.myapp.repository.MstRegionRepository;
import com.mycompany.myapp.repository.search.MstRegionSearchRepository;
import com.mycompany.myapp.service.MstRegionService;
import com.mycompany.myapp.service.dto.MstRegionDTO;
import com.mycompany.myapp.service.mapper.MstRegionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstRegion}.
 */
@Service
@Transactional
public class MstRegionServiceImpl implements MstRegionService {

    private static final Logger log = LoggerFactory.getLogger(MstRegionServiceImpl.class);

    private final MstRegionRepository mstRegionRepository;

    private final MstRegionMapper mstRegionMapper;

    private final MstRegionSearchRepository mstRegionSearchRepository;

    public MstRegionServiceImpl(
        MstRegionRepository mstRegionRepository,
        MstRegionMapper mstRegionMapper,
        MstRegionSearchRepository mstRegionSearchRepository
    ) {
        this.mstRegionRepository = mstRegionRepository;
        this.mstRegionMapper = mstRegionMapper;
        this.mstRegionSearchRepository = mstRegionSearchRepository;
    }

    @Override
    public Mono<MstRegionDTO> save(MstRegionDTO mstRegionDTO) {
        log.debug("Request to save MstRegion : {}", mstRegionDTO);
        return mstRegionRepository
            .save(mstRegionMapper.toEntity(mstRegionDTO))
            .flatMap(mstRegionSearchRepository::save)
            .map(mstRegionMapper::toDto);
    }

    @Override
    public Mono<MstRegionDTO> update(MstRegionDTO mstRegionDTO) {
        log.debug("Request to update MstRegion : {}", mstRegionDTO);
        return mstRegionRepository
            .save(mstRegionMapper.toEntity(mstRegionDTO))
            .flatMap(mstRegionSearchRepository::save)
            .map(mstRegionMapper::toDto);
    }

    @Override
    public Mono<MstRegionDTO> partialUpdate(MstRegionDTO mstRegionDTO) {
        log.debug("Request to partially update MstRegion : {}", mstRegionDTO);

        return mstRegionRepository
            .findById(mstRegionDTO.getId())
            .map(existingMstRegion -> {
                mstRegionMapper.partialUpdate(existingMstRegion, mstRegionDTO);

                return existingMstRegion;
            })
            .flatMap(mstRegionRepository::save)
            .flatMap(savedMstRegion -> {
                mstRegionSearchRepository.save(savedMstRegion);
                return Mono.just(savedMstRegion);
            })
            .map(mstRegionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstRegionDTO> findByCriteria(MstRegionCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstRegions by Criteria");
        return mstRegionRepository.findByCriteria(criteria, pageable).map(mstRegionMapper::toDto);
    }

    /**
     * Find the count of mstRegions by criteria.
     * @param criteria filtering criteria
     * @return the count of mstRegions
     */
    public Mono<Long> countByCriteria(MstRegionCriteria criteria) {
        log.debug("Request to get the count of all MstRegions by Criteria");
        return mstRegionRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstRegionRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstRegionSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstRegionDTO> findOne(Long id) {
        log.debug("Request to get MstRegion : {}", id);
        return mstRegionRepository.findById(id).map(mstRegionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstRegion : {}", id);
        return mstRegionRepository.deleteById(id).then(mstRegionSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstRegionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstRegions for query {}", query);
        return mstRegionSearchRepository.search(query, pageable).map(mstRegionMapper::toDto);
    }
}
