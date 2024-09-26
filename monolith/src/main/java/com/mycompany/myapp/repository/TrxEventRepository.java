package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxEvent;
import com.mycompany.myapp.domain.criteria.TrxEventCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxEventRepository extends ReactiveCrudRepository<TrxEvent, Long>, TrxEventRepositoryInternal {
    @Query("SELECT * FROM trx_event entity WHERE entity.service_id = :id")
    Flux<TrxEvent> findByService(Long id);

    @Query("SELECT * FROM trx_event entity WHERE entity.service_id IS NULL")
    Flux<TrxEvent> findAllWhereServiceIsNull();

    @Query("SELECT * FROM trx_event entity WHERE entity.testimonial_id = :id")
    Flux<TrxEvent> findByTestimonial(Long id);

    @Query("SELECT * FROM trx_event entity WHERE entity.testimonial_id IS NULL")
    Flux<TrxEvent> findAllWhereTestimonialIsNull();

    @Override
    <S extends TrxEvent> Mono<S> save(S entity);

    @Override
    Flux<TrxEvent> findAll();

    @Override
    Mono<TrxEvent> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxEventRepositoryInternal {
    <S extends TrxEvent> Mono<S> save(S entity);

    Flux<TrxEvent> findAllBy(Pageable pageable);

    Flux<TrxEvent> findAll();

    Mono<TrxEvent> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxEvent> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxEvent> findByCriteria(TrxEventCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxEventCriteria criteria);
}
