package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.District;
import com.mycompany.myapp.domain.criteria.DistrictCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the District entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DistrictRepository extends ReactiveCrudRepository<District, Long>, DistrictRepositoryInternal {
    Flux<District> findAllBy(Pageable pageable);

    @Override
    Mono<District> findOneWithEagerRelationships(Long id);

    @Override
    Flux<District> findAllWithEagerRelationships();

    @Override
    Flux<District> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM district entity WHERE entity.city_id = :id")
    Flux<District> findByCity(Long id);

    @Query("SELECT * FROM district entity WHERE entity.city_id IS NULL")
    Flux<District> findAllWhereCityIsNull();

    @Override
    <S extends District> Mono<S> save(S entity);

    @Override
    Flux<District> findAll();

    @Override
    Mono<District> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DistrictRepositoryInternal {
    <S extends District> Mono<S> save(S entity);

    Flux<District> findAllBy(Pageable pageable);

    Flux<District> findAll();

    Mono<District> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<District> findAllBy(Pageable pageable, Criteria criteria);
    Flux<District> findByCriteria(DistrictCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(DistrictCriteria criteria);

    Mono<District> findOneWithEagerRelationships(Long id);

    Flux<District> findAllWithEagerRelationships();

    Flux<District> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
