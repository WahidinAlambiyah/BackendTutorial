package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxTestimonial;
import com.mycompany.myapp.domain.criteria.TrxTestimonialCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxTestimonial entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxTestimonialRepository extends ReactiveCrudRepository<TrxTestimonial, Long>, TrxTestimonialRepositoryInternal {
    @Override
    <S extends TrxTestimonial> Mono<S> save(S entity);

    @Override
    Flux<TrxTestimonial> findAll();

    @Override
    Mono<TrxTestimonial> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxTestimonialRepositoryInternal {
    <S extends TrxTestimonial> Mono<S> save(S entity);

    Flux<TrxTestimonial> findAllBy(Pageable pageable);

    Flux<TrxTestimonial> findAll();

    Mono<TrxTestimonial> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxTestimonial> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxTestimonial> findByCriteria(TrxTestimonialCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxTestimonialCriteria criteria);
}
