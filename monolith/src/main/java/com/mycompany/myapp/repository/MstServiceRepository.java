package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstService;
import com.mycompany.myapp.domain.criteria.MstServiceCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstService entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstServiceRepository extends ReactiveCrudRepository<MstService, Long>, MstServiceRepositoryInternal {
    Flux<MstService> findAllBy(Pageable pageable);

    @Query("SELECT * FROM mst_service entity WHERE entity.testimonial_id = :id")
    Flux<MstService> findByTestimonial(Long id);

    @Query("SELECT * FROM mst_service entity WHERE entity.testimonial_id IS NULL")
    Flux<MstService> findAllWhereTestimonialIsNull();

    @Override
    <S extends MstService> Mono<S> save(S entity);

    @Override
    Flux<MstService> findAll();

    @Override
    Mono<MstService> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstServiceRepositoryInternal {
    <S extends MstService> Mono<S> save(S entity);

    Flux<MstService> findAllBy(Pageable pageable);

    Flux<MstService> findAll();

    Mono<MstService> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstService> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstService> findByCriteria(MstServiceCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstServiceCriteria criteria);
}
