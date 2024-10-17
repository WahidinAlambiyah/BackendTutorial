package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxDiscount;
import com.mycompany.myapp.domain.criteria.TrxDiscountCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxDiscount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxDiscountRepository extends ReactiveCrudRepository<TrxDiscount, Long>, TrxDiscountRepositoryInternal {
    Flux<TrxDiscount> findAllBy(Pageable pageable);

    @Override
    <S extends TrxDiscount> Mono<S> save(S entity);

    @Override
    Flux<TrxDiscount> findAll();

    @Override
    Mono<TrxDiscount> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxDiscountRepositoryInternal {
    <S extends TrxDiscount> Mono<S> save(S entity);

    Flux<TrxDiscount> findAllBy(Pageable pageable);

    Flux<TrxDiscount> findAll();

    Mono<TrxDiscount> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxDiscount> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxDiscount> findByCriteria(TrxDiscountCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxDiscountCriteria criteria);
}
