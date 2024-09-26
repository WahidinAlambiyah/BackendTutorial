package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstSubDistrict;
import com.mycompany.myapp.domain.criteria.MstSubDistrictCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstSubDistrict entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstSubDistrictRepository extends ReactiveCrudRepository<MstSubDistrict, Long>, MstSubDistrictRepositoryInternal {
    Flux<MstSubDistrict> findAllBy(Pageable pageable);

    @Override
    Mono<MstSubDistrict> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MstSubDistrict> findAllWithEagerRelationships();

    @Override
    Flux<MstSubDistrict> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mst_sub_district entity WHERE entity.district_id = :id")
    Flux<MstSubDistrict> findByDistrict(Long id);

    @Query("SELECT * FROM mst_sub_district entity WHERE entity.district_id IS NULL")
    Flux<MstSubDistrict> findAllWhereDistrictIsNull();

    @Override
    <S extends MstSubDistrict> Mono<S> save(S entity);

    @Override
    Flux<MstSubDistrict> findAll();

    @Override
    Mono<MstSubDistrict> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstSubDistrictRepositoryInternal {
    <S extends MstSubDistrict> Mono<S> save(S entity);

    Flux<MstSubDistrict> findAllBy(Pageable pageable);

    Flux<MstSubDistrict> findAll();

    Mono<MstSubDistrict> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstSubDistrict> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstSubDistrict> findByCriteria(MstSubDistrictCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstSubDistrictCriteria criteria);

    Mono<MstSubDistrict> findOneWithEagerRelationships(Long id);

    Flux<MstSubDistrict> findAllWithEagerRelationships();

    Flux<MstSubDistrict> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
