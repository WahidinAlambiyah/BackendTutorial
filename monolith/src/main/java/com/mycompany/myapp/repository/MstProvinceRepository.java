package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstProvince;
import com.mycompany.myapp.domain.criteria.MstProvinceCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstProvince entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstProvinceRepository extends ReactiveCrudRepository<MstProvince, Long>, MstProvinceRepositoryInternal {
    Flux<MstProvince> findAllBy(Pageable pageable);

    @Override
    Mono<MstProvince> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MstProvince> findAllWithEagerRelationships();

    @Override
    Flux<MstProvince> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mst_province entity WHERE entity.country_id = :id")
    Flux<MstProvince> findByCountry(Long id);

    @Query("SELECT * FROM mst_province entity WHERE entity.country_id IS NULL")
    Flux<MstProvince> findAllWhereCountryIsNull();

    @Override
    <S extends MstProvince> Mono<S> save(S entity);

    @Override
    Flux<MstProvince> findAll();

    @Override
    Mono<MstProvince> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstProvinceRepositoryInternal {
    <S extends MstProvince> Mono<S> save(S entity);

    Flux<MstProvince> findAllBy(Pageable pageable);

    Flux<MstProvince> findAll();

    Mono<MstProvince> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstProvince> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstProvince> findByCriteria(MstProvinceCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstProvinceCriteria criteria);

    Mono<MstProvince> findOneWithEagerRelationships(Long id);

    Flux<MstProvince> findAllWithEagerRelationships();

    Flux<MstProvince> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
