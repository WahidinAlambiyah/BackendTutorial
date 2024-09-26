package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstCity;
import com.mycompany.myapp.domain.criteria.MstCityCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstCity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstCityRepository extends ReactiveCrudRepository<MstCity, Long>, MstCityRepositoryInternal {
    Flux<MstCity> findAllBy(Pageable pageable);

    @Override
    Mono<MstCity> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MstCity> findAllWithEagerRelationships();

    @Override
    Flux<MstCity> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mst_city entity WHERE entity.province_id = :id")
    Flux<MstCity> findByProvince(Long id);

    @Query("SELECT * FROM mst_city entity WHERE entity.province_id IS NULL")
    Flux<MstCity> findAllWhereProvinceIsNull();

    @Override
    <S extends MstCity> Mono<S> save(S entity);

    @Override
    Flux<MstCity> findAll();

    @Override
    Mono<MstCity> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstCityRepositoryInternal {
    <S extends MstCity> Mono<S> save(S entity);

    Flux<MstCity> findAllBy(Pageable pageable);

    Flux<MstCity> findAll();

    Mono<MstCity> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstCity> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstCity> findByCriteria(MstCityCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstCityCriteria criteria);

    Mono<MstCity> findOneWithEagerRelationships(Long id);

    Flux<MstCity> findAllWithEagerRelationships();

    Flux<MstCity> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
