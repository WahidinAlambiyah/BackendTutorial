package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.SubDistrict;
import com.mycompany.myapp.repository.SubDistrictRepository;
import com.mycompany.myapp.repository.search.SubDistrictSearchRepository;
import com.mycompany.myapp.service.SubDistrictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.SubDistrict}.
 */
@Service
@Transactional
public class SubDistrictServiceImpl implements SubDistrictService {

    private static final Logger log = LoggerFactory.getLogger(SubDistrictServiceImpl.class);

    private final SubDistrictRepository subDistrictRepository;

    private final SubDistrictSearchRepository subDistrictSearchRepository;

    public SubDistrictServiceImpl(SubDistrictRepository subDistrictRepository, SubDistrictSearchRepository subDistrictSearchRepository) {
        this.subDistrictRepository = subDistrictRepository;
        this.subDistrictSearchRepository = subDistrictSearchRepository;
    }

    @Override
    public Mono<SubDistrict> save(SubDistrict subDistrict) {
        log.debug("Request to save SubDistrict : {}", subDistrict);
        return subDistrictRepository.save(subDistrict).flatMap(subDistrictSearchRepository::save);
    }

    @Override
    public Mono<SubDistrict> update(SubDistrict subDistrict) {
        log.debug("Request to update SubDistrict : {}", subDistrict);
        return subDistrictRepository.save(subDistrict).flatMap(subDistrictSearchRepository::save);
    }

    @Override
    public Mono<SubDistrict> partialUpdate(SubDistrict subDistrict) {
        log.debug("Request to partially update SubDistrict : {}", subDistrict);

        return subDistrictRepository
            .findById(subDistrict.getId())
            .map(existingSubDistrict -> {
                if (subDistrict.getName() != null) {
                    existingSubDistrict.setName(subDistrict.getName());
                }
                if (subDistrict.getCode() != null) {
                    existingSubDistrict.setCode(subDistrict.getCode());
                }

                return existingSubDistrict;
            })
            .flatMap(subDistrictRepository::save)
            .flatMap(savedSubDistrict -> {
                subDistrictSearchRepository.save(savedSubDistrict);
                return Mono.just(savedSubDistrict);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SubDistrict> findAll(Pageable pageable) {
        log.debug("Request to get all SubDistricts");
        return subDistrictRepository.findAllBy(pageable);
    }

    public Flux<SubDistrict> findAllWithEagerRelationships(Pageable pageable) {
        return subDistrictRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return subDistrictRepository.count();
    }

    public Mono<Long> searchCount() {
        return subDistrictSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SubDistrict> findOne(Long id) {
        log.debug("Request to get SubDistrict : {}", id);
        return subDistrictRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete SubDistrict : {}", id);
        return subDistrictRepository.deleteById(id).then(subDistrictSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SubDistrict> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SubDistricts for query {}", query);
        return subDistrictSearchRepository.search(query, pageable);
    }
}
