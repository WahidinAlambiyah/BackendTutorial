package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Province;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Province entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProvinceRepository extends ReactiveCrudRepository<Province, Long>, ProvinceRepositoryInternal {
    Flux<Province> findAllBy(Pageable pageable);

    @Override
    Mono<Province> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Province> findAllWithEagerRelationships();

    @Override
    Flux<Province> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM province entity WHERE entity.country_id = :id")
    Flux<Province> findByCountry(Long id);

    @Query("SELECT * FROM province entity WHERE entity.country_id IS NULL")
    Flux<Province> findAllWhereCountryIsNull();

    @Override
    <S extends Province> Mono<S> save(S entity);

    @Override
    Flux<Province> findAll();

    @Override
    Mono<Province> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProvinceRepositoryInternal {
    <S extends Province> Mono<S> save(S entity);

    Flux<Province> findAllBy(Pageable pageable);

    Flux<Province> findAll();

    Mono<Province> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Province> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Province> findOneWithEagerRelationships(Long id);

    Flux<Province> findAllWithEagerRelationships();

    Flux<Province> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
