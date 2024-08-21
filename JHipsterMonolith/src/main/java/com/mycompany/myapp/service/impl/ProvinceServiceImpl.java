package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Province;
import com.mycompany.myapp.repository.ProvinceRepository;
import com.mycompany.myapp.repository.search.ProvinceSearchRepository;
import com.mycompany.myapp.service.ProvinceService;
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

    private final ProvinceSearchRepository provinceSearchRepository;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository, ProvinceSearchRepository provinceSearchRepository) {
        this.provinceRepository = provinceRepository;
        this.provinceSearchRepository = provinceSearchRepository;
    }

    @Override
    public Mono<Province> save(Province province) {
        log.debug("Request to save Province : {}", province);
        return provinceRepository.save(province).flatMap(provinceSearchRepository::save);
    }

    @Override
    public Mono<Province> update(Province province) {
        log.debug("Request to update Province : {}", province);
        return provinceRepository.save(province).flatMap(provinceSearchRepository::save);
    }

    @Override
    public Mono<Province> partialUpdate(Province province) {
        log.debug("Request to partially update Province : {}", province);

        return provinceRepository
            .findById(province.getId())
            .map(existingProvince -> {
                if (province.getName() != null) {
                    existingProvince.setName(province.getName());
                }
                if (province.getCode() != null) {
                    existingProvince.setCode(province.getCode());
                }

                return existingProvince;
            })
            .flatMap(provinceRepository::save)
            .flatMap(savedProvince -> {
                provinceSearchRepository.save(savedProvince);
                return Mono.just(savedProvince);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Province> findAll(Pageable pageable) {
        log.debug("Request to get all Provinces");
        return provinceRepository.findAllBy(pageable);
    }

    public Flux<Province> findAllWithEagerRelationships(Pageable pageable) {
        return provinceRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return provinceRepository.count();
    }

    public Mono<Long> searchCount() {
        return provinceSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Province> findOne(Long id) {
        log.debug("Request to get Province : {}", id);
        return provinceRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Province : {}", id);
        return provinceRepository.deleteById(id).then(provinceSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Province> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Provinces for query {}", query);
        return provinceSearchRepository.search(query, pageable);
    }
}
