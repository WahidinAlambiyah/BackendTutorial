package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstDistrict;
import com.mycompany.myapp.domain.criteria.MstDistrictCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstDistrict entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstDistrictRepository extends ReactiveCrudRepository<MstDistrict, Long>, MstDistrictRepositoryInternal {
    Flux<MstDistrict> findAllBy(Pageable pageable);

    @Override
    Mono<MstDistrict> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MstDistrict> findAllWithEagerRelationships();

    @Override
    Flux<MstDistrict> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mst_district entity WHERE entity.city_id = :id")
    Flux<MstDistrict> findByCity(Long id);

    @Query("SELECT * FROM mst_district entity WHERE entity.city_id IS NULL")
    Flux<MstDistrict> findAllWhereCityIsNull();

    @Override
    <S extends MstDistrict> Mono<S> save(S entity);

    @Override
    Flux<MstDistrict> findAll();

    @Override
    Mono<MstDistrict> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstDistrictRepositoryInternal {
    <S extends MstDistrict> Mono<S> save(S entity);

    Flux<MstDistrict> findAllBy(Pageable pageable);

    Flux<MstDistrict> findAll();

    Mono<MstDistrict> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstDistrict> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstDistrict> findByCriteria(MstDistrictCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstDistrictCriteria criteria);

    Mono<MstDistrict> findOneWithEagerRelationships(Long id);

    Flux<MstDistrict> findAllWithEagerRelationships();

    Flux<MstDistrict> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
