package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxOrderItem;
import com.mycompany.myapp.domain.criteria.TrxOrderItemCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxOrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxOrderItemRepository extends ReactiveCrudRepository<TrxOrderItem, Long>, TrxOrderItemRepositoryInternal {
    Flux<TrxOrderItem> findAllBy(Pageable pageable);

    @Query("SELECT * FROM trx_order_item entity WHERE entity.order_id = :id")
    Flux<TrxOrderItem> findByOrder(Long id);

    @Query("SELECT * FROM trx_order_item entity WHERE entity.order_id IS NULL")
    Flux<TrxOrderItem> findAllWhereOrderIsNull();

    @Query("SELECT * FROM trx_order_item entity WHERE entity.product_id = :id")
    Flux<TrxOrderItem> findByProduct(Long id);

    @Query("SELECT * FROM trx_order_item entity WHERE entity.product_id IS NULL")
    Flux<TrxOrderItem> findAllWhereProductIsNull();

    @Override
    <S extends TrxOrderItem> Mono<S> save(S entity);

    @Override
    Flux<TrxOrderItem> findAll();

    @Override
    Mono<TrxOrderItem> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxOrderItemRepositoryInternal {
    <S extends TrxOrderItem> Mono<S> save(S entity);

    Flux<TrxOrderItem> findAllBy(Pageable pageable);

    Flux<TrxOrderItem> findAll();

    Mono<TrxOrderItem> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxOrderItem> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxOrderItem> findByCriteria(TrxOrderItemCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxOrderItemCriteria criteria);
}
