package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.ProvinceCriteria;
import com.mycompany.myapp.repository.ProvinceRepository;
import com.mycompany.myapp.repository.search.ProvinceSearchRepository;
import com.mycompany.myapp.service.ProvinceService;
import com.mycompany.myapp.service.dto.ProvinceDTO;
import com.mycompany.myapp.service.mapper.ProvinceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Province}.
 */
@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

    private static final Logger log = LoggerFactory.getLogger(ProvinceServiceImpl.class);

    private final ProvinceRepository provinceRepository;

    private final ProvinceMapper provinceMapper;

    private final ProvinceSearchRepository provinceSearchRepository;

    public ProvinceServiceImpl(
        ProvinceRepository provinceRepository,
        ProvinceMapper provinceMapper,
        ProvinceSearchRepository provinceSearchRepository
    ) {
        this.provinceRepository = provinceRepository;
        this.provinceMapper = provinceMapper;
        this.provinceSearchRepository = provinceSearchRepository;
    }

    @Override
    public Mono<ProvinceDTO> save(ProvinceDTO provinceDTO) {
        log.debug("Request to save Province : {}", provinceDTO);
        return provinceRepository
            .save(provinceMapper.toEntity(provinceDTO))
            .flatMap(provinceSearchRepository::save)
            .map(provinceMapper::toDto);
    }

    @Override
    public Mono<ProvinceDTO> update(ProvinceDTO provinceDTO) {
        log.debug("Request to update Province : {}", provinceDTO);
        return provinceRepository
            .save(provinceMapper.toEntity(provinceDTO))
            .flatMap(provinceSearchRepository::save)
            .map(provinceMapper::toDto);
    }

    @Override
    public Mono<ProvinceDTO> partialUpdate(ProvinceDTO provinceDTO) {
        log.debug("Request to partially update Province : {}", provinceDTO);

        return provinceRepository
            .findById(provinceDTO.getId())
            .map(existingProvince -> {
                provinceMapper.partialUpdate(existingProvince, provinceDTO);

                return existingProvince;
            })
            .flatMap(provinceRepository::save)
            .flatMap(savedProvince -> {
                provinceSearchRepository.save(savedProvince);
                return Mono.just(savedProvince);
            })
            .map(provinceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProvinceDTO> findByCriteria(ProvinceCriteria criteria, Pageable pageable) {
        log.debug("Request to get all Provinces by Criteria");
        return provinceRepository.findByCriteria(criteria, pageable).map(provinceMapper::toDto);
    }

    /**
     * Find the count of provinces by criteria.
     * @param criteria filtering criteria
     * @return the count of provinces
     */
    public Mono<Long> countByCriteria(ProvinceCriteria criteria) {
        log.debug("Request to get the count of all Provinces by Criteria");
        return provinceRepository.countByCriteria(criteria);
    }

    public Flux<ProvinceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return provinceRepository.findAllWithEagerRelationships(pageable).map(provinceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return provinceRepository.count();
    }

    public Mono<Long> searchCount() {
        return provinceSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProvinceDTO> findOne(Long id) {
        log.debug("Request to get Province : {}", id);
        return provinceRepository.findOneWithEagerRelationships(id).map(provinceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Province : {}", id);
        return provinceRepository.deleteById(id).then(provinceSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProvinceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Provinces for query {}", query);
        return provinceSearchRepository.search(query, pageable).map(provinceMapper::toDto);
    }
}
