package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstCountryCriteria;
import com.mycompany.myapp.repository.MstCountryRepository;
import com.mycompany.myapp.repository.search.MstCountrySearchRepository;
import com.mycompany.myapp.service.MstCountryService;
import com.mycompany.myapp.service.dto.MstCountryDTO;
import com.mycompany.myapp.service.mapper.MstCountryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstCountry}.
 */
@Service
@Transactional
public class MstCountryServiceImpl implements MstCountryService {

    private static final Logger log = LoggerFactory.getLogger(MstCountryServiceImpl.class);

    private final MstCountryRepository mstCountryRepository;

    private final MstCountryMapper mstCountryMapper;

    private final MstCountrySearchRepository mstCountrySearchRepository;

    public MstCountryServiceImpl(
        MstCountryRepository mstCountryRepository,
        MstCountryMapper mstCountryMapper,
        MstCountrySearchRepository mstCountrySearchRepository
    ) {
        this.mstCountryRepository = mstCountryRepository;
        this.mstCountryMapper = mstCountryMapper;
        this.mstCountrySearchRepository = mstCountrySearchRepository;
    }

    @Override
    public Mono<MstCountryDTO> save(MstCountryDTO mstCountryDTO) {
        log.debug("Request to save MstCountry : {}", mstCountryDTO);
        return mstCountryRepository
            .save(mstCountryMapper.toEntity(mstCountryDTO))
            .flatMap(mstCountrySearchRepository::save)
            .map(mstCountryMapper::toDto);
    }

    @Override
    public Mono<MstCountryDTO> update(MstCountryDTO mstCountryDTO) {
        log.debug("Request to update MstCountry : {}", mstCountryDTO);
        return mstCountryRepository
            .save(mstCountryMapper.toEntity(mstCountryDTO))
            .flatMap(mstCountrySearchRepository::save)
            .map(mstCountryMapper::toDto);
    }

    @Override
    public Mono<MstCountryDTO> partialUpdate(MstCountryDTO mstCountryDTO) {
        log.debug("Request to partially update MstCountry : {}", mstCountryDTO);

        return mstCountryRepository
            .findById(mstCountryDTO.getId())
            .map(existingMstCountry -> {
                mstCountryMapper.partialUpdate(existingMstCountry, mstCountryDTO);

                return existingMstCountry;
            })
            .flatMap(mstCountryRepository::save)
            .flatMap(savedMstCountry -> {
                mstCountrySearchRepository.save(savedMstCountry);
                return Mono.just(savedMstCountry);
            })
            .map(mstCountryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstCountryDTO> findByCriteria(MstCountryCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstCountries by Criteria");
        return mstCountryRepository.findByCriteria(criteria, pageable).map(mstCountryMapper::toDto);
    }

    /**
     * Find the count of mstCountries by criteria.
     * @param criteria filtering criteria
     * @return the count of mstCountries
     */
    public Mono<Long> countByCriteria(MstCountryCriteria criteria) {
        log.debug("Request to get the count of all MstCountries by Criteria");
        return mstCountryRepository.countByCriteria(criteria);
    }

    public Flux<MstCountryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mstCountryRepository.findAllWithEagerRelationships(pageable).map(mstCountryMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstCountryRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstCountrySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstCountryDTO> findOne(Long id) {
        log.debug("Request to get MstCountry : {}", id);
        return mstCountryRepository.findOneWithEagerRelationships(id).map(mstCountryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstCountry : {}", id);
        return mstCountryRepository.deleteById(id).then(mstCountrySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstCountryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstCountries for query {}", query);
        return mstCountrySearchRepository.search(query, pageable).map(mstCountryMapper::toDto);
    }
}
