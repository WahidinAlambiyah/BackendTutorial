package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Region;
import com.mycompany.myapp.repository.RegionRepository;
import com.mycompany.myapp.repository.search.RegionSearchRepository;
import com.mycompany.myapp.service.RegionService;
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

    private final RegionSearchRepository regionSearchRepository;

    public RegionServiceImpl(RegionRepository regionRepository, RegionSearchRepository regionSearchRepository) {
        this.regionRepository = regionRepository;
        this.regionSearchRepository = regionSearchRepository;
    }

    @Override
    public Mono<Region> save(Region region) {
        log.debug("Request to save Region : {}", region);
        return regionRepository.save(region).flatMap(regionSearchRepository::save);
    }

    @Override
    public Mono<Region> update(Region region) {
        log.debug("Request to update Region : {}", region);
        return regionRepository.save(region).flatMap(regionSearchRepository::save);
    }

    @Override
    public Mono<Region> partialUpdate(Region region) {
        log.debug("Request to partially update Region : {}", region);

        return regionRepository
            .findById(region.getId())
            .map(existingRegion -> {
                if (region.getName() != null) {
                    existingRegion.setName(region.getName());
                }
                if (region.getCode() != null) {
                    existingRegion.setCode(region.getCode());
                }

                return existingRegion;
            })
            .flatMap(regionRepository::save)
            .flatMap(savedRegion -> {
                regionSearchRepository.save(savedRegion);
                return Mono.just(savedRegion);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Region> findAll(Pageable pageable) {
        log.debug("Request to get all Regions");
        return regionRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return regionRepository.count();
    }

    public Mono<Long> searchCount() {
        return regionSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Region> findOne(Long id) {
        log.debug("Request to get Region : {}", id);
        return regionRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Region : {}", id);
        return regionRepository.deleteById(id).then(regionSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Region> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Regions for query {}", query);
        return regionSearchRepository.search(query, pageable);
    }
}
