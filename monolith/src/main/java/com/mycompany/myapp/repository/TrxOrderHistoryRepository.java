package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxOrderHistory;
import com.mycompany.myapp.domain.criteria.TrxOrderHistoryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxOrderHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxOrderHistoryRepository extends ReactiveCrudRepository<TrxOrderHistory, Long>, TrxOrderHistoryRepositoryInternal {
    Flux<TrxOrderHistory> findAllBy(Pageable pageable);

    @Override
    <S extends TrxOrderHistory> Mono<S> save(S entity);

    @Override
    Flux<TrxOrderHistory> findAll();

    @Override
    Mono<TrxOrderHistory> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxOrderHistoryRepositoryInternal {
    <S extends TrxOrderHistory> Mono<S> save(S entity);

    Flux<TrxOrderHistory> findAllBy(Pageable pageable);

    Flux<TrxOrderHistory> findAll();

    Mono<TrxOrderHistory> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxOrderHistory> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxOrderHistory> findByCriteria(TrxOrderHistoryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxOrderHistoryCriteria criteria);
}
