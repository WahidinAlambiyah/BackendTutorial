package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.CityCriteria;
import com.mycompany.myapp.repository.CityRepository;
import com.mycompany.myapp.repository.search.CitySearchRepository;
import com.mycompany.myapp.service.CityService;
import com.mycompany.myapp.service.dto.CityDTO;
import com.mycompany.myapp.service.mapper.CityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.City}.
 */
@Service
@Transactional
public class CityServiceImpl implements CityService {

    private static final Logger log = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    private final CitySearchRepository citySearchRepository;

    public CityServiceImpl(CityRepository cityRepository, CityMapper cityMapper, CitySearchRepository citySearchRepository) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.citySearchRepository = citySearchRepository;
    }

    @Override
    public Mono<CityDTO> save(CityDTO cityDTO) {
        log.debug("Request to save City : {}", cityDTO);
        return cityRepository.save(cityMapper.toEntity(cityDTO)).flatMap(citySearchRepository::save).map(cityMapper::toDto);
    }

    @Override
    public Mono<CityDTO> update(CityDTO cityDTO) {
        log.debug("Request to update City : {}", cityDTO);
        return cityRepository.save(cityMapper.toEntity(cityDTO)).flatMap(citySearchRepository::save).map(cityMapper::toDto);
    }

    @Override
    public Mono<CityDTO> partialUpdate(CityDTO cityDTO) {
        log.debug("Request to partially update City : {}", cityDTO);

        return cityRepository
            .findById(cityDTO.getId())
            .map(existingCity -> {
                cityMapper.partialUpdate(existingCity, cityDTO);

                return existingCity;
            })
            .flatMap(cityRepository::save)
            .flatMap(savedCity -> {
                citySearchRepository.save(savedCity);
                return Mono.just(savedCity);
            })
            .map(cityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CityDTO> findByCriteria(CityCriteria criteria, Pageable pageable) {
        log.debug("Request to get all Cities by Criteria");
        return cityRepository.findByCriteria(criteria, pageable).map(cityMapper::toDto);
    }

    /**
     * Find the count of cities by criteria.
     * @param criteria filtering criteria
     * @return the count of cities
     */
    public Mono<Long> countByCriteria(CityCriteria criteria) {
        log.debug("Request to get the count of all Cities by Criteria");
        return cityRepository.countByCriteria(criteria);
    }

    public Flux<CityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return cityRepository.findAllWithEagerRelationships(pageable).map(cityMapper::toDto);
    }

    public Mono<Long> countAll() {
        return cityRepository.count();
    }

    public Mono<Long> searchCount() {
        return citySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CityDTO> findOne(Long id) {
        log.debug("Request to get City : {}", id);
        return cityRepository.findOneWithEagerRelationships(id).map(cityMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete City : {}", id);
        return cityRepository.deleteById(id).then(citySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Cities for query {}", query);
        return citySearchRepository.search(query, pageable).map(cityMapper::toDto);
    }
}
