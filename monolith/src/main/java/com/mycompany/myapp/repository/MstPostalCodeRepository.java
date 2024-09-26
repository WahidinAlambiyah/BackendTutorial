package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstPostalCode;
import com.mycompany.myapp.domain.criteria.MstPostalCodeCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstPostalCode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstPostalCodeRepository extends ReactiveCrudRepository<MstPostalCode, Long>, MstPostalCodeRepositoryInternal {
    Flux<MstPostalCode> findAllBy(Pageable pageable);

    @Override
    Mono<MstPostalCode> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MstPostalCode> findAllWithEagerRelationships();

    @Override
    Flux<MstPostalCode> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mst_postal_code entity WHERE entity.sub_district_id = :id")
    Flux<MstPostalCode> findBySubDistrict(Long id);

    @Query("SELECT * FROM mst_postal_code entity WHERE entity.sub_district_id IS NULL")
    Flux<MstPostalCode> findAllWhereSubDistrictIsNull();

    @Override
    <S extends MstPostalCode> Mono<S> save(S entity);

    @Override
    Flux<MstPostalCode> findAll();

    @Override
    Mono<MstPostalCode> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstPostalCodeRepositoryInternal {
    <S extends MstPostalCode> Mono<S> save(S entity);

    Flux<MstPostalCode> findAllBy(Pageable pageable);

    Flux<MstPostalCode> findAll();

    Mono<MstPostalCode> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstPostalCode> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstPostalCode> findByCriteria(MstPostalCodeCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstPostalCodeCriteria criteria);

    Mono<MstPostalCode> findOneWithEagerRelationships(Long id);

    Flux<MstPostalCode> findAllWithEagerRelationships();

    Flux<MstPostalCode> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
