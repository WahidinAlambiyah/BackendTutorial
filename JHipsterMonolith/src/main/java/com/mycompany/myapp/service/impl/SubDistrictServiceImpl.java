package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.SubDistrictCriteria;
import com.mycompany.myapp.repository.SubDistrictRepository;
import com.mycompany.myapp.repository.search.SubDistrictSearchRepository;
import com.mycompany.myapp.service.SubDistrictService;
import com.mycompany.myapp.service.dto.SubDistrictDTO;
import com.mycompany.myapp.service.mapper.SubDistrictMapper;
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

    private final SubDistrictMapper subDistrictMapper;

    private final SubDistrictSearchRepository subDistrictSearchRepository;

    public SubDistrictServiceImpl(
        SubDistrictRepository subDistrictRepository,
        SubDistrictMapper subDistrictMapper,
        SubDistrictSearchRepository subDistrictSearchRepository
    ) {
        this.subDistrictRepository = subDistrictRepository;
        this.subDistrictMapper = subDistrictMapper;
        this.subDistrictSearchRepository = subDistrictSearchRepository;
    }

    @Override
    public Mono<SubDistrictDTO> save(SubDistrictDTO subDistrictDTO) {
        log.debug("Request to save SubDistrict : {}", subDistrictDTO);
        return subDistrictRepository
            .save(subDistrictMapper.toEntity(subDistrictDTO))
            .flatMap(subDistrictSearchRepository::save)
            .map(subDistrictMapper::toDto);
    }

    @Override
    public Mono<SubDistrictDTO> update(SubDistrictDTO subDistrictDTO) {
        log.debug("Request to update SubDistrict : {}", subDistrictDTO);
        return subDistrictRepository
            .save(subDistrictMapper.toEntity(subDistrictDTO))
            .flatMap(subDistrictSearchRepository::save)
            .map(subDistrictMapper::toDto);
    }

    @Override
    public Mono<SubDistrictDTO> partialUpdate(SubDistrictDTO subDistrictDTO) {
        log.debug("Request to partially update SubDistrict : {}", subDistrictDTO);

        return subDistrictRepository
            .findById(subDistrictDTO.getId())
            .map(existingSubDistrict -> {
                subDistrictMapper.partialUpdate(existingSubDistrict, subDistrictDTO);

                return existingSubDistrict;
            })
            .flatMap(subDistrictRepository::save)
            .flatMap(savedSubDistrict -> {
                subDistrictSearchRepository.save(savedSubDistrict);
                return Mono.just(savedSubDistrict);
            })
            .map(subDistrictMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SubDistrictDTO> findByCriteria(SubDistrictCriteria criteria, Pageable pageable) {
        log.debug("Request to get all SubDistricts by Criteria");
        return subDistrictRepository.findByCriteria(criteria, pageable).map(subDistrictMapper::toDto);
    }

    /**
     * Find the count of subDistricts by criteria.
     * @param criteria filtering criteria
     * @return the count of subDistricts
     */
    public Mono<Long> countByCriteria(SubDistrictCriteria criteria) {
        log.debug("Request to get the count of all SubDistricts by Criteria");
        return subDistrictRepository.countByCriteria(criteria);
    }

    public Flux<SubDistrictDTO> findAllWithEagerRelationships(Pageable pageable) {
        return subDistrictRepository.findAllWithEagerRelationships(pageable).map(subDistrictMapper::toDto);
    }

    public Mono<Long> countAll() {
        return subDistrictRepository.count();
    }

    public Mono<Long> searchCount() {
        return subDistrictSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SubDistrictDTO> findOne(Long id) {
        log.debug("Request to get SubDistrict : {}", id);
        return subDistrictRepository.findOneWithEagerRelationships(id).map(subDistrictMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete SubDistrict : {}", id);
        return subDistrictRepository.deleteById(id).then(subDistrictSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SubDistrictDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SubDistricts for query {}", query);
        return subDistrictSearchRepository.search(query, pageable).map(subDistrictMapper::toDto);
    }
}
