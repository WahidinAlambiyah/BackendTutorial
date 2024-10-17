package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxOrderStock;
import com.mycompany.myapp.domain.criteria.TrxOrderStockCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxOrderStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxOrderStockRepository extends ReactiveCrudRepository<TrxOrderStock, Long>, TrxOrderStockRepositoryInternal {
    Flux<TrxOrderStock> findAllBy(Pageable pageable);

    @Query("SELECT * FROM trx_order_stock entity WHERE entity.supplier_id = :id")
    Flux<TrxOrderStock> findBySupplier(Long id);

    @Query("SELECT * FROM trx_order_stock entity WHERE entity.supplier_id IS NULL")
    Flux<TrxOrderStock> findAllWhereSupplierIsNull();

    @Override
    <S extends TrxOrderStock> Mono<S> save(S entity);

    @Override
    Flux<TrxOrderStock> findAll();

    @Override
    Mono<TrxOrderStock> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxOrderStockRepositoryInternal {
    <S extends TrxOrderStock> Mono<S> save(S entity);

    Flux<TrxOrderStock> findAllBy(Pageable pageable);

    Flux<TrxOrderStock> findAll();

    Mono<TrxOrderStock> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxOrderStock> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxOrderStock> findByCriteria(TrxOrderStockCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxOrderStockCriteria criteria);
}
