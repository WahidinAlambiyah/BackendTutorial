package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxCoupon;
import com.mycompany.myapp.domain.criteria.TrxCouponCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxCoupon entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxCouponRepository extends ReactiveCrudRepository<TrxCoupon, Long>, TrxCouponRepositoryInternal {
    Flux<TrxCoupon> findAllBy(Pageable pageable);

    @Override
    <S extends TrxCoupon> Mono<S> save(S entity);

    @Override
    Flux<TrxCoupon> findAll();

    @Override
    Mono<TrxCoupon> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxCouponRepositoryInternal {
    <S extends TrxCoupon> Mono<S> save(S entity);

    Flux<TrxCoupon> findAllBy(Pageable pageable);

    Flux<TrxCoupon> findAll();

    Mono<TrxCoupon> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxCoupon> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxCoupon> findByCriteria(TrxCouponCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxCouponCriteria criteria);
}
