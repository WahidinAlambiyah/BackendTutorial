package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstCityCriteria;
import com.mycompany.myapp.repository.MstCityRepository;
import com.mycompany.myapp.repository.search.MstCitySearchRepository;
import com.mycompany.myapp.service.MstCityService;
import com.mycompany.myapp.service.dto.MstCityDTO;
import com.mycompany.myapp.service.mapper.MstCityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstCity}.
 */
@Service
@Transactional
public class MstCityServiceImpl implements MstCityService {

    private static final Logger log = LoggerFactory.getLogger(MstCityServiceImpl.class);

    private final MstCityRepository mstCityRepository;

    private final MstCityMapper mstCityMapper;

    private final MstCitySearchRepository mstCitySearchRepository;

    public MstCityServiceImpl(
        MstCityRepository mstCityRepository,
        MstCityMapper mstCityMapper,
        MstCitySearchRepository mstCitySearchRepository
    ) {
        this.mstCityRepository = mstCityRepository;
        this.mstCityMapper = mstCityMapper;
        this.mstCitySearchRepository = mstCitySearchRepository;
    }

    @Override
    public Mono<MstCityDTO> save(MstCityDTO mstCityDTO) {
        log.debug("Request to save MstCity : {}", mstCityDTO);
        return mstCityRepository.save(mstCityMapper.toEntity(mstCityDTO)).flatMap(mstCitySearchRepository::save).map(mstCityMapper::toDto);
    }

    @Override
    public Mono<MstCityDTO> update(MstCityDTO mstCityDTO) {
        log.debug("Request to update MstCity : {}", mstCityDTO);
        return mstCityRepository.save(mstCityMapper.toEntity(mstCityDTO)).flatMap(mstCitySearchRepository::save).map(mstCityMapper::toDto);
    }

    @Override
    public Mono<MstCityDTO> partialUpdate(MstCityDTO mstCityDTO) {
        log.debug("Request to partially update MstCity : {}", mstCityDTO);

        return mstCityRepository
            .findById(mstCityDTO.getId())
            .map(existingMstCity -> {
                mstCityMapper.partialUpdate(existingMstCity, mstCityDTO);

                return existingMstCity;
            })
            .flatMap(mstCityRepository::save)
            .flatMap(savedMstCity -> {
                mstCitySearchRepository.save(savedMstCity);
                return Mono.just(savedMstCity);
            })
            .map(mstCityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstCityDTO> findByCriteria(MstCityCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstCities by Criteria");
        return mstCityRepository.findByCriteria(criteria, pageable).map(mstCityMapper::toDto);
    }

    /**
     * Find the count of mstCities by criteria.
     * @param criteria filtering criteria
     * @return the count of mstCities
     */
    public Mono<Long> countByCriteria(MstCityCriteria criteria) {
        log.debug("Request to get the count of all MstCities by Criteria");
        return mstCityRepository.countByCriteria(criteria);
    }

    public Flux<MstCityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mstCityRepository.findAllWithEagerRelationships(pageable).map(mstCityMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstCityRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstCitySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstCityDTO> findOne(Long id) {
        log.debug("Request to get MstCity : {}", id);
        return mstCityRepository.findOneWithEagerRelationships(id).map(mstCityMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstCity : {}", id);
        return mstCityRepository.deleteById(id).then(mstCitySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstCityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstCities for query {}", query);
        return mstCitySearchRepository.search(query, pageable).map(mstCityMapper::toDto);
    }
}
