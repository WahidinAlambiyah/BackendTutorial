package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxDelivery;
import com.mycompany.myapp.domain.criteria.TrxDeliveryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxDelivery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxDeliveryRepository extends ReactiveCrudRepository<TrxDelivery, Long>, TrxDeliveryRepositoryInternal {
    Flux<TrxDelivery> findAllBy(Pageable pageable);

    @Query("SELECT * FROM trx_delivery entity WHERE entity.driver_id = :id")
    Flux<TrxDelivery> findByDriver(Long id);

    @Query("SELECT * FROM trx_delivery entity WHERE entity.driver_id IS NULL")
    Flux<TrxDelivery> findAllWhereDriverIsNull();

    @Query("SELECT * FROM trx_delivery entity WHERE entity.trx_order_id = :id")
    Flux<TrxDelivery> findByTrxOrder(Long id);

    @Query("SELECT * FROM trx_delivery entity WHERE entity.trx_order_id IS NULL")
    Flux<TrxDelivery> findAllWhereTrxOrderIsNull();

    @Override
    <S extends TrxDelivery> Mono<S> save(S entity);

    @Override
    Flux<TrxDelivery> findAll();

    @Override
    Mono<TrxDelivery> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxDeliveryRepositoryInternal {
    <S extends TrxDelivery> Mono<S> save(S entity);

    Flux<TrxDelivery> findAllBy(Pageable pageable);

    Flux<TrxDelivery> findAll();

    Mono<TrxDelivery> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxDelivery> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxDelivery> findByCriteria(TrxDeliveryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxDeliveryCriteria criteria);
}
