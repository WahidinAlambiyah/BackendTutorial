package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Country;
import com.mycompany.myapp.repository.CountryRepository;
import com.mycompany.myapp.repository.search.CountrySearchRepository;
import com.mycompany.myapp.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Country}.
 */
@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);

    private final CountryRepository countryRepository;

    private final CountrySearchRepository countrySearchRepository;

    public CountryServiceImpl(CountryRepository countryRepository, CountrySearchRepository countrySearchRepository) {
        this.countryRepository = countryRepository;
        this.countrySearchRepository = countrySearchRepository;
    }

    @Override
    public Mono<Country> save(Country country) {
        log.debug("Request to save Country : {}", country);
        return countryRepository.save(country).flatMap(countrySearchRepository::save);
    }

    @Override
    public Mono<Country> update(Country country) {
        log.debug("Request to update Country : {}", country);
        return countryRepository.save(country).flatMap(countrySearchRepository::save);
    }

    @Override
    public Mono<Country> partialUpdate(Country country) {
        log.debug("Request to partially update Country : {}", country);

        return countryRepository
            .findById(country.getId())
            .map(existingCountry -> {
                if (country.getName() != null) {
                    existingCountry.setName(country.getName());
                }
                if (country.getCode() != null) {
                    existingCountry.setCode(country.getCode());
                }

                return existingCountry;
            })
            .flatMap(countryRepository::save)
            .flatMap(savedCountry -> {
                countrySearchRepository.save(savedCountry);
                return Mono.just(savedCountry);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Country> findAll(Pageable pageable) {
        log.debug("Request to get all Countries");
        return countryRepository.findAllBy(pageable);
    }

    public Flux<Country> findAllWithEagerRelationships(Pageable pageable) {
        return countryRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return countryRepository.count();
    }

    public Mono<Long> searchCount() {
        return countrySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Country> findOne(Long id) {
        log.debug("Request to get Country : {}", id);
        return countryRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Country : {}", id);
        return countryRepository.deleteById(id).then(countrySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Country> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Countries for query {}", query);
        return countrySearchRepository.search(query, pageable);
    }
}
