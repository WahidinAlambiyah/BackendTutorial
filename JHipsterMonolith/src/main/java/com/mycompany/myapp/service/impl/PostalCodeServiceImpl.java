package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.PostalCodeCriteria;
import com.mycompany.myapp.repository.PostalCodeRepository;
import com.mycompany.myapp.repository.search.PostalCodeSearchRepository;
import com.mycompany.myapp.service.PostalCodeService;
import com.mycompany.myapp.service.dto.PostalCodeDTO;
import com.mycompany.myapp.service.mapper.PostalCodeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PostalCode}.
 */
@Service
@Transactional
public class PostalCodeServiceImpl implements PostalCodeService {

    private static final Logger log = LoggerFactory.getLogger(PostalCodeServiceImpl.class);

    private final PostalCodeRepository postalCodeRepository;

    private final PostalCodeMapper postalCodeMapper;

    private final PostalCodeSearchRepository postalCodeSearchRepository;

    public PostalCodeServiceImpl(
        PostalCodeRepository postalCodeRepository,
        PostalCodeMapper postalCodeMapper,
        PostalCodeSearchRepository postalCodeSearchRepository
    ) {
        this.postalCodeRepository = postalCodeRepository;
        this.postalCodeMapper = postalCodeMapper;
        this.postalCodeSearchRepository = postalCodeSearchRepository;
    }

    @Override
    public Mono<PostalCodeDTO> save(PostalCodeDTO postalCodeDTO) {
        log.debug("Request to save PostalCode : {}", postalCodeDTO);
        return postalCodeRepository
            .save(postalCodeMapper.toEntity(postalCodeDTO))
            .flatMap(postalCodeSearchRepository::save)
            .map(postalCodeMapper::toDto);
    }

    @Override
    public Mono<PostalCodeDTO> update(PostalCodeDTO postalCodeDTO) {
        log.debug("Request to update PostalCode : {}", postalCodeDTO);
        return postalCodeRepository
            .save(postalCodeMapper.toEntity(postalCodeDTO))
            .flatMap(postalCodeSearchRepository::save)
            .map(postalCodeMapper::toDto);
    }

    @Override
    public Mono<PostalCodeDTO> partialUpdate(PostalCodeDTO postalCodeDTO) {
        log.debug("Request to partially update PostalCode : {}", postalCodeDTO);

        return postalCodeRepository
            .findById(postalCodeDTO.getId())
            .map(existingPostalCode -> {
                postalCodeMapper.partialUpdate(existingPostalCode, postalCodeDTO);

                return existingPostalCode;
            })
            .flatMap(postalCodeRepository::save)
            .flatMap(savedPostalCode -> {
                postalCodeSearchRepository.save(savedPostalCode);
                return Mono.just(savedPostalCode);
            })
            .map(postalCodeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PostalCodeDTO> findByCriteria(PostalCodeCriteria criteria, Pageable pageable) {
        log.debug("Request to get all PostalCodes by Criteria");
        return postalCodeRepository.findByCriteria(criteria, pageable).map(postalCodeMapper::toDto);
    }

    /**
     * Find the count of postalCodes by criteria.
     * @param criteria filtering criteria
     * @return the count of postalCodes
     */
    public Mono<Long> countByCriteria(PostalCodeCriteria criteria) {
        log.debug("Request to get the count of all PostalCodes by Criteria");
        return postalCodeRepository.countByCriteria(criteria);
    }

    public Flux<PostalCodeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return postalCodeRepository.findAllWithEagerRelationships(pageable).map(postalCodeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return postalCodeRepository.count();
    }

    public Mono<Long> searchCount() {
        return postalCodeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PostalCodeDTO> findOne(Long id) {
        log.debug("Request to get PostalCode : {}", id);
        return postalCodeRepository.findOneWithEagerRelationships(id).map(postalCodeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete PostalCode : {}", id);
        return postalCodeRepository.deleteById(id).then(postalCodeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PostalCodeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PostalCodes for query {}", query);
        return postalCodeSearchRepository.search(query, pageable).map(postalCodeMapper::toDto);
    }
}
