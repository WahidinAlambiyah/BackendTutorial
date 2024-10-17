package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.domain.criteria.TrxOrderCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxOrderRepository extends ReactiveCrudRepository<TrxOrder, Long>, TrxOrderRepositoryInternal {
    Flux<TrxOrder> findAllBy(Pageable pageable);

    @Query("SELECT * FROM trx_order entity WHERE entity.mst_customer_id = :id")
    Flux<TrxOrder> findByMstCustomer(Long id);

    @Query("SELECT * FROM trx_order entity WHERE entity.mst_customer_id IS NULL")
    Flux<TrxOrder> findAllWhereMstCustomerIsNull();

    @Override
    <S extends TrxOrder> Mono<S> save(S entity);

    @Override
    Flux<TrxOrder> findAll();

    @Override
    Mono<TrxOrder> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxOrderRepositoryInternal {
    <S extends TrxOrder> Mono<S> save(S entity);

    Flux<TrxOrder> findAllBy(Pageable pageable);

    Flux<TrxOrder> findAll();

    Mono<TrxOrder> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxOrder> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxOrder> findByCriteria(TrxOrderCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxOrderCriteria criteria);
}
