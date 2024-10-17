package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxCart;
import com.mycompany.myapp.domain.criteria.TrxCartCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxCart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxCartRepository extends ReactiveCrudRepository<TrxCart, Long>, TrxCartRepositoryInternal {
    Flux<TrxCart> findAllBy(Pageable pageable);

    @Query("SELECT * FROM trx_cart entity WHERE entity.customer_id = :id")
    Flux<TrxCart> findByCustomer(Long id);

    @Query("SELECT * FROM trx_cart entity WHERE entity.customer_id IS NULL")
    Flux<TrxCart> findAllWhereCustomerIsNull();

    @Override
    <S extends TrxCart> Mono<S> save(S entity);

    @Override
    Flux<TrxCart> findAll();

    @Override
    Mono<TrxCart> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxCartRepositoryInternal {
    <S extends TrxCart> Mono<S> save(S entity);

    Flux<TrxCart> findAllBy(Pageable pageable);

    Flux<TrxCart> findAll();

    Mono<TrxCart> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxCart> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxCart> findByCriteria(TrxCartCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxCartCriteria criteria);
}
