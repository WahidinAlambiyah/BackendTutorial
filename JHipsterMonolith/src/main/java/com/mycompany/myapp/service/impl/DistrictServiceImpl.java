package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.DistrictCriteria;
import com.mycompany.myapp.repository.DistrictRepository;
import com.mycompany.myapp.repository.search.DistrictSearchRepository;
import com.mycompany.myapp.service.DistrictService;
import com.mycompany.myapp.service.dto.DistrictDTO;
import com.mycompany.myapp.service.mapper.DistrictMapper;
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

    private final DistrictMapper districtMapper;

    private final DistrictSearchRepository districtSearchRepository;

    public DistrictServiceImpl(
        DistrictRepository districtRepository,
        DistrictMapper districtMapper,
        DistrictSearchRepository districtSearchRepository
    ) {
        this.districtRepository = districtRepository;
        this.districtMapper = districtMapper;
        this.districtSearchRepository = districtSearchRepository;
    }

    @Override
    public Mono<DistrictDTO> save(DistrictDTO districtDTO) {
        log.debug("Request to save District : {}", districtDTO);
        return districtRepository
            .save(districtMapper.toEntity(districtDTO))
            .flatMap(districtSearchRepository::save)
            .map(districtMapper::toDto);
    }

    @Override
    public Mono<DistrictDTO> update(DistrictDTO districtDTO) {
        log.debug("Request to update District : {}", districtDTO);
        return districtRepository
            .save(districtMapper.toEntity(districtDTO))
            .flatMap(districtSearchRepository::save)
            .map(districtMapper::toDto);
    }

    @Override
    public Mono<DistrictDTO> partialUpdate(DistrictDTO districtDTO) {
        log.debug("Request to partially update District : {}", districtDTO);

        return districtRepository
            .findById(districtDTO.getId())
            .map(existingDistrict -> {
                districtMapper.partialUpdate(existingDistrict, districtDTO);

                return existingDistrict;
            })
            .flatMap(districtRepository::save)
            .flatMap(savedDistrict -> {
                districtSearchRepository.save(savedDistrict);
                return Mono.just(savedDistrict);
            })
            .map(districtMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DistrictDTO> findByCriteria(DistrictCriteria criteria, Pageable pageable) {
        log.debug("Request to get all Districts by Criteria");
        return districtRepository.findByCriteria(criteria, pageable).map(districtMapper::toDto);
    }

    /**
     * Find the count of districts by criteria.
     * @param criteria filtering criteria
     * @return the count of districts
     */
    public Mono<Long> countByCriteria(DistrictCriteria criteria) {
        log.debug("Request to get the count of all Districts by Criteria");
        return districtRepository.countByCriteria(criteria);
    }

    public Flux<DistrictDTO> findAllWithEagerRelationships(Pageable pageable) {
        return districtRepository.findAllWithEagerRelationships(pageable).map(districtMapper::toDto);
    }

    public Mono<Long> countAll() {
        return districtRepository.count();
    }

    public Mono<Long> searchCount() {
        return districtSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DistrictDTO> findOne(Long id) {
        log.debug("Request to get District : {}", id);
        return districtRepository.findOneWithEagerRelationships(id).map(districtMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete District : {}", id);
        return districtRepository.deleteById(id).then(districtSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DistrictDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Districts for query {}", query);
        return districtSearchRepository.search(query, pageable).map(districtMapper::toDto);
    }
}
