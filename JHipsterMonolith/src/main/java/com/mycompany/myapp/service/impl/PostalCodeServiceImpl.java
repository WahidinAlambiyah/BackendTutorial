package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.PostalCode;
import com.mycompany.myapp.repository.PostalCodeRepository;
import com.mycompany.myapp.repository.search.PostalCodeSearchRepository;
import com.mycompany.myapp.service.PostalCodeService;
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

    private final PostalCodeSearchRepository postalCodeSearchRepository;

    public PostalCodeServiceImpl(PostalCodeRepository postalCodeRepository, PostalCodeSearchRepository postalCodeSearchRepository) {
        this.postalCodeRepository = postalCodeRepository;
        this.postalCodeSearchRepository = postalCodeSearchRepository;
    }

    @Override
    public Mono<PostalCode> save(PostalCode postalCode) {
        log.debug("Request to save PostalCode : {}", postalCode);
        return postalCodeRepository.save(postalCode).flatMap(postalCodeSearchRepository::save);
    }

    @Override
    public Mono<PostalCode> update(PostalCode postalCode) {
        log.debug("Request to update PostalCode : {}", postalCode);
        return postalCodeRepository.save(postalCode).flatMap(postalCodeSearchRepository::save);
    }

    @Override
    public Mono<PostalCode> partialUpdate(PostalCode postalCode) {
        log.debug("Request to partially update PostalCode : {}", postalCode);

        return postalCodeRepository
            .findById(postalCode.getId())
            .map(existingPostalCode -> {
                if (postalCode.getCode() != null) {
                    existingPostalCode.setCode(postalCode.getCode());
                }

                return existingPostalCode;
            })
            .flatMap(postalCodeRepository::save)
            .flatMap(savedPostalCode -> {
                postalCodeSearchRepository.save(savedPostalCode);
                return Mono.just(savedPostalCode);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PostalCode> findAll(Pageable pageable) {
        log.debug("Request to get all PostalCodes");
        return postalCodeRepository.findAllBy(pageable);
    }

    public Flux<PostalCode> findAllWithEagerRelationships(Pageable pageable) {
        return postalCodeRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return postalCodeRepository.count();
    }

    public Mono<Long> searchCount() {
        return postalCodeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PostalCode> findOne(Long id) {
        log.debug("Request to get PostalCode : {}", id);
        return postalCodeRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete PostalCode : {}", id);
        return postalCodeRepository.deleteById(id).then(postalCodeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PostalCode> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PostalCodes for query {}", query);
        return postalCodeSearchRepository.search(query, pageable);
    }
}
