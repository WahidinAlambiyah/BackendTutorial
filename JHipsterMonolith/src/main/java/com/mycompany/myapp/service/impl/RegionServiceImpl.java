package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.RegionCriteria;
import com.mycompany.myapp.repository.RegionRepository;
import com.mycompany.myapp.repository.search.RegionSearchRepository;
import com.mycompany.myapp.service.RegionService;
import com.mycompany.myapp.service.dto.RegionDTO;
import com.mycompany.myapp.service.mapper.RegionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Region}.
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService {

    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);

    private final RegionRepository regionRepository;

    private final RegionMapper regionMapper;

    private final RegionSearchRepository regionSearchRepository;

    public RegionServiceImpl(RegionRepository regionRepository, RegionMapper regionMapper, RegionSearchRepository regionSearchRepository) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
        this.regionSearchRepository = regionSearchRepository;
    }

    @Override
    public Mono<RegionDTO> save(RegionDTO regionDTO) {
        log.debug("Request to save Region : {}", regionDTO);
        return regionRepository.save(regionMapper.toEntity(regionDTO)).flatMap(regionSearchRepository::save).map(regionMapper::toDto);
    }

    @Override
    public Mono<RegionDTO> update(RegionDTO regionDTO) {
        log.debug("Request to update Region : {}", regionDTO);
        return regionRepository.save(regionMapper.toEntity(regionDTO)).flatMap(regionSearchRepository::save).map(regionMapper::toDto);
    }

    @Override
    public Mono<RegionDTO> partialUpdate(RegionDTO regionDTO) {
        log.debug("Request to partially update Region : {}", regionDTO);

        return regionRepository
            .findById(regionDTO.getId())
            .map(existingRegion -> {
                regionMapper.partialUpdate(existingRegion, regionDTO);

                return existingRegion;
            })
            .flatMap(regionRepository::save)
            .flatMap(savedRegion -> {
                regionSearchRepository.save(savedRegion);
                return Mono.just(savedRegion);
            })
            .map(regionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RegionDTO> findByCriteria(RegionCriteria criteria, Pageable pageable) {
        log.debug("Request to get all Regions by Criteria");
        return regionRepository.findByCriteria(criteria, pageable).map(regionMapper::toDto);
    }

    /**
     * Find the count of regions by criteria.
     * @param criteria filtering criteria
     * @return the count of regions
     */
    public Mono<Long> countByCriteria(RegionCriteria criteria) {
        log.debug("Request to get the count of all Regions by Criteria");
        return regionRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return regionRepository.count();
    }

    public Mono<Long> searchCount() {
        return regionSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RegionDTO> findOne(Long id) {
        log.debug("Request to get Region : {}", id);
        return regionRepository.findById(id).map(regionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Region : {}", id);
        return regionRepository.deleteById(id).then(regionSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RegionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Regions for query {}", query);
        return regionSearchRepository.search(query, pageable).map(regionMapper::toDto);
    }
}
