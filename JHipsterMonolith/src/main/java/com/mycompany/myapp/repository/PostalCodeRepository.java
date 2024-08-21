package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PostalCode;
import com.mycompany.myapp.domain.criteria.PostalCodeCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PostalCode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostalCodeRepository extends ReactiveCrudRepository<PostalCode, Long>, PostalCodeRepositoryInternal {
    Flux<PostalCode> findAllBy(Pageable pageable);

    @Override
    Mono<PostalCode> findOneWithEagerRelationships(Long id);

    @Override
    Flux<PostalCode> findAllWithEagerRelationships();

    @Override
    Flux<PostalCode> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM postal_code entity WHERE entity.sub_district_id = :id")
    Flux<PostalCode> findBySubDistrict(Long id);

    @Query("SELECT * FROM postal_code entity WHERE entity.sub_district_id IS NULL")
    Flux<PostalCode> findAllWhereSubDistrictIsNull();

    @Override
    <S extends PostalCode> Mono<S> save(S entity);

    @Override
    Flux<PostalCode> findAll();

    @Override
    Mono<PostalCode> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PostalCodeRepositoryInternal {
    <S extends PostalCode> Mono<S> save(S entity);

    Flux<PostalCode> findAllBy(Pageable pageable);

    Flux<PostalCode> findAll();

    Mono<PostalCode> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PostalCode> findAllBy(Pageable pageable, Criteria criteria);
    Flux<PostalCode> findByCriteria(PostalCodeCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(PostalCodeCriteria criteria);

    Mono<PostalCode> findOneWithEagerRelationships(Long id);

    Flux<PostalCode> findAllWithEagerRelationships();

    Flux<PostalCode> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
