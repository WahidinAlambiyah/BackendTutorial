package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.District;
import com.mycompany.myapp.repository.DistrictRepository;
import com.mycompany.myapp.repository.search.DistrictSearchRepository;
import com.mycompany.myapp.service.DistrictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.District}.
 */
@Service
@Transactional
public class DistrictServiceImpl implements DistrictService {

    private static final Logger log = LoggerFactory.getLogger(DistrictServiceImpl.class);

    private final DistrictRepository districtRepository;

    private final DistrictSearchRepository districtSearchRepository;

    public DistrictServiceImpl(DistrictRepository districtRepository, DistrictSearchRepository districtSearchRepository) {
        this.districtRepository = districtRepository;
        this.districtSearchRepository = districtSearchRepository;
    }

    @Override
    public Mono<District> save(District district) {
        log.debug("Request to save District : {}", district);
        return districtRepository.save(district).flatMap(districtSearchRepository::save);
    }

    @Override
    public Mono<District> update(District district) {
        log.debug("Request to update District : {}", district);
        return districtRepository.save(district).flatMap(districtSearchRepository::save);
    }

    @Override
    public Mono<District> partialUpdate(District district) {
        log.debug("Request to partially update District : {}", district);

        return districtRepository
            .findById(district.getId())
            .map(existingDistrict -> {
                if (district.getName() != null) {
                    existingDistrict.setName(district.getName());
                }
                if (district.getCode() != null) {
                    existingDistrict.setCode(district.getCode());
                }

                return existingDistrict;
            })
            .flatMap(districtRepository::save)
            .flatMap(savedDistrict -> {
                districtSearchRepository.save(savedDistrict);
                return Mono.just(savedDistrict);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<District> findAll(Pageable pageable) {
        log.debug("Request to get all Districts");
        return districtRepository.findAllBy(pageable);
    }

    public Flux<District> findAllWithEagerRelationships(Pageable pageable) {
        return districtRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return districtRepository.count();
    }

    public Mono<Long> searchCount() {
        return districtSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<District> findOne(Long id) {
        log.debug("Request to get District : {}", id);
        return districtRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete District : {}", id);
        return districtRepository.deleteById(id).then(districtSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<District> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Districts for query {}", query);
        return districtSearchRepository.search(query, pageable);
    }
}
