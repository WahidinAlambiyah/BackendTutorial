package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.City;
import com.mycompany.myapp.repository.CityRepository;
import com.mycompany.myapp.repository.search.CitySearchRepository;
import com.mycompany.myapp.service.CityService;
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

    private final CitySearchRepository citySearchRepository;

    public CityServiceImpl(CityRepository cityRepository, CitySearchRepository citySearchRepository) {
        this.cityRepository = cityRepository;
        this.citySearchRepository = citySearchRepository;
    }

    @Override
    public Mono<City> save(City city) {
        log.debug("Request to save City : {}", city);
        return cityRepository.save(city).flatMap(citySearchRepository::save);
    }

    @Override
    public Mono<City> update(City city) {
        log.debug("Request to update City : {}", city);
        return cityRepository.save(city).flatMap(citySearchRepository::save);
    }

    @Override
    public Mono<City> partialUpdate(City city) {
        log.debug("Request to partially update City : {}", city);

        return cityRepository
            .findById(city.getId())
            .map(existingCity -> {
                if (city.getName() != null) {
                    existingCity.setName(city.getName());
                }
                if (city.getCode() != null) {
                    existingCity.setCode(city.getCode());
                }

                return existingCity;
            })
            .flatMap(cityRepository::save)
            .flatMap(savedCity -> {
                citySearchRepository.save(savedCity);
                return Mono.just(savedCity);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<City> findAll(Pageable pageable) {
        log.debug("Request to get all Cities");
        return cityRepository.findAllBy(pageable);
    }

    public Flux<City> findAllWithEagerRelationships(Pageable pageable) {
        return cityRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return cityRepository.count();
    }

    public Mono<Long> searchCount() {
        return citySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<City> findOne(Long id) {
        log.debug("Request to get City : {}", id);
        return cityRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete City : {}", id);
        return cityRepository.deleteById(id).then(citySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<City> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Cities for query {}", query);
        return citySearchRepository.search(query, pageable);
    }
}
