package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstPostalCodeCriteria;
import com.mycompany.myapp.repository.MstPostalCodeRepository;
import com.mycompany.myapp.repository.search.MstPostalCodeSearchRepository;
import com.mycompany.myapp.service.MstPostalCodeService;
import com.mycompany.myapp.service.dto.MstPostalCodeDTO;
import com.mycompany.myapp.service.mapper.MstPostalCodeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstPostalCode}.
 */
@Service
@Transactional
public class MstPostalCodeServiceImpl implements MstPostalCodeService {

    private static final Logger log = LoggerFactory.getLogger(MstPostalCodeServiceImpl.class);

    private final MstPostalCodeRepository mstPostalCodeRepository;

    private final MstPostalCodeMapper mstPostalCodeMapper;

    private final MstPostalCodeSearchRepository mstPostalCodeSearchRepository;

    public MstPostalCodeServiceImpl(
        MstPostalCodeRepository mstPostalCodeRepository,
        MstPostalCodeMapper mstPostalCodeMapper,
        MstPostalCodeSearchRepository mstPostalCodeSearchRepository
    ) {
        this.mstPostalCodeRepository = mstPostalCodeRepository;
        this.mstPostalCodeMapper = mstPostalCodeMapper;
        this.mstPostalCodeSearchRepository = mstPostalCodeSearchRepository;
    }

    @Override
    public Mono<MstPostalCodeDTO> save(MstPostalCodeDTO mstPostalCodeDTO) {
        log.debug("Request to save MstPostalCode : {}", mstPostalCodeDTO);
        return mstPostalCodeRepository
            .save(mstPostalCodeMapper.toEntity(mstPostalCodeDTO))
            .flatMap(mstPostalCodeSearchRepository::save)
            .map(mstPostalCodeMapper::toDto);
    }

    @Override
    public Mono<MstPostalCodeDTO> update(MstPostalCodeDTO mstPostalCodeDTO) {
        log.debug("Request to update MstPostalCode : {}", mstPostalCodeDTO);
        return mstPostalCodeRepository
            .save(mstPostalCodeMapper.toEntity(mstPostalCodeDTO))
            .flatMap(mstPostalCodeSearchRepository::save)
            .map(mstPostalCodeMapper::toDto);
    }

    @Override
    public Mono<MstPostalCodeDTO> partialUpdate(MstPostalCodeDTO mstPostalCodeDTO) {
        log.debug("Request to partially update MstPostalCode : {}", mstPostalCodeDTO);

        return mstPostalCodeRepository
            .findById(mstPostalCodeDTO.getId())
            .map(existingMstPostalCode -> {
                mstPostalCodeMapper.partialUpdate(existingMstPostalCode, mstPostalCodeDTO);

                return existingMstPostalCode;
            })
            .flatMap(mstPostalCodeRepository::save)
            .flatMap(savedMstPostalCode -> {
                mstPostalCodeSearchRepository.save(savedMstPostalCode);
                return Mono.just(savedMstPostalCode);
            })
            .map(mstPostalCodeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstPostalCodeDTO> findByCriteria(MstPostalCodeCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstPostalCodes by Criteria");
        return mstPostalCodeRepository.findByCriteria(criteria, pageable).map(mstPostalCodeMapper::toDto);
    }

    /**
     * Find the count of mstPostalCodes by criteria.
     * @param criteria filtering criteria
     * @return the count of mstPostalCodes
     */
    public Mono<Long> countByCriteria(MstPostalCodeCriteria criteria) {
        log.debug("Request to get the count of all MstPostalCodes by Criteria");
        return mstPostalCodeRepository.countByCriteria(criteria);
    }

    public Flux<MstPostalCodeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mstPostalCodeRepository.findAllWithEagerRelationships(pageable).map(mstPostalCodeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstPostalCodeRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstPostalCodeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstPostalCodeDTO> findOne(Long id) {
        log.debug("Request to get MstPostalCode : {}", id);
        return mstPostalCodeRepository.findOneWithEagerRelationships(id).map(mstPostalCodeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstPostalCode : {}", id);
        return mstPostalCodeRepository.deleteById(id).then(mstPostalCodeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstPostalCodeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstPostalCodes for query {}", query);
        return mstPostalCodeSearchRepository.search(query, pageable).map(mstPostalCodeMapper::toDto);
    }
}
