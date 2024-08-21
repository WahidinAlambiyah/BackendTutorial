package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SubDistrict;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SubDistrict entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubDistrictRepository extends ReactiveCrudRepository<SubDistrict, Long>, SubDistrictRepositoryInternal {
    Flux<SubDistrict> findAllBy(Pageable pageable);

    @Override
    Mono<SubDistrict> findOneWithEagerRelationships(Long id);

    @Override
    Flux<SubDistrict> findAllWithEagerRelationships();

    @Override
    Flux<SubDistrict> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM sub_district entity WHERE entity.district_id = :id")
    Flux<SubDistrict> findByDistrict(Long id);

    @Query("SELECT * FROM sub_district entity WHERE entity.district_id IS NULL")
    Flux<SubDistrict> findAllWhereDistrictIsNull();

    @Override
    <S extends SubDistrict> Mono<S> save(S entity);

    @Override
    Flux<SubDistrict> findAll();

    @Override
    Mono<SubDistrict> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SubDistrictRepositoryInternal {
    <S extends SubDistrict> Mono<S> save(S entity);

    Flux<SubDistrict> findAllBy(Pageable pageable);

    Flux<SubDistrict> findAll();

    Mono<SubDistrict> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<SubDistrict> findAllBy(Pageable pageable, Criteria criteria);

    Mono<SubDistrict> findOneWithEagerRelationships(Long id);

    Flux<SubDistrict> findAllWithEagerRelationships();

    Flux<SubDistrict> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
