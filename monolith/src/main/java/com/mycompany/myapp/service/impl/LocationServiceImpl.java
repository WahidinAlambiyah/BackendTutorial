package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.LocationCriteria;
import com.mycompany.myapp.repository.LocationRepository;
import com.mycompany.myapp.repository.search.LocationSearchRepository;
import com.mycompany.myapp.service.LocationService;
import com.mycompany.myapp.service.dto.LocationDTO;
import com.mycompany.myapp.service.mapper.LocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Location}.
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    private final LocationSearchRepository locationSearchRepository;

    public LocationServiceImpl(
        LocationRepository locationRepository,
        LocationMapper locationMapper,
        LocationSearchRepository locationSearchRepository
    ) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.locationSearchRepository = locationSearchRepository;
    }

    @Override
    public Mono<LocationDTO> save(LocationDTO locationDTO) {
        log.debug("Request to save Location : {}", locationDTO);
        return locationRepository
            .save(locationMapper.toEntity(locationDTO))
            .flatMap(locationSearchRepository::save)
            .map(locationMapper::toDto);
    }

    @Override
    public Mono<LocationDTO> update(LocationDTO locationDTO) {
        log.debug("Request to update Location : {}", locationDTO);
        return locationRepository
            .save(locationMapper.toEntity(locationDTO))
            .flatMap(locationSearchRepository::save)
            .map(locationMapper::toDto);
    }

    @Override
    public Mono<LocationDTO> partialUpdate(LocationDTO locationDTO) {
        log.debug("Request to partially update Location : {}", locationDTO);

        return locationRepository
            .findById(locationDTO.getId())
            .map(existingLocation -> {
                locationMapper.partialUpdate(existingLocation, locationDTO);

                return existingLocation;
            })
            .flatMap(locationRepository::save)
            .flatMap(savedLocation -> {
                locationSearchRepository.save(savedLocation);
                return Mono.just(savedLocation);
            })
            .map(locationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LocationDTO> findByCriteria(LocationCriteria criteria, Pageable pageable) {
        log.debug("Request to get all Locations by Criteria");
        return locationRepository.findByCriteria(criteria, pageable).map(locationMapper::toDto);
    }

    /**
     * Find the count of locations by criteria.
     * @param criteria filtering criteria
     * @return the count of locations
     */
    public Mono<Long> countByCriteria(LocationCriteria criteria) {
        log.debug("Request to get the count of all Locations by Criteria");
        return locationRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the locations where MstDepartment is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<LocationDTO> findAllWhereMstDepartmentIsNull() {
        log.debug("Request to get all locations where MstDepartment is null");
        return locationRepository.findAllWhereMstDepartmentIsNull().map(locationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return locationRepository.count();
    }

    public Mono<Long> searchCount() {
        return locationSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LocationDTO> findOne(Long id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id).map(locationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Location : {}", id);
        return locationRepository.deleteById(id).then(locationSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LocationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Locations for query {}", query);
        return locationSearchRepository.search(query, pageable).map(locationMapper::toDto);
    }
}
