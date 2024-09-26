package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstCountry;
import com.mycompany.myapp.domain.criteria.MstCountryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstCountry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstCountryRepository extends ReactiveCrudRepository<MstCountry, Long>, MstCountryRepositoryInternal {
    Flux<MstCountry> findAllBy(Pageable pageable);

    @Override
    Mono<MstCountry> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MstCountry> findAllWithEagerRelationships();

    @Override
    Flux<MstCountry> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mst_country entity WHERE entity.region_id = :id")
    Flux<MstCountry> findByRegion(Long id);

    @Query("SELECT * FROM mst_country entity WHERE entity.region_id IS NULL")
    Flux<MstCountry> findAllWhereRegionIsNull();

    @Override
    <S extends MstCountry> Mono<S> save(S entity);

    @Override
    Flux<MstCountry> findAll();

    @Override
    Mono<MstCountry> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstCountryRepositoryInternal {
    <S extends MstCountry> Mono<S> save(S entity);

    Flux<MstCountry> findAllBy(Pageable pageable);

    Flux<MstCountry> findAll();

    Mono<MstCountry> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstCountry> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstCountry> findByCriteria(MstCountryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstCountryCriteria criteria);

    Mono<MstCountry> findOneWithEagerRelationships(Long id);

    Flux<MstCountry> findAllWithEagerRelationships();

    Flux<MstCountry> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
