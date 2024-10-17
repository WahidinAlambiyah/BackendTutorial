package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxProductHistory;
import com.mycompany.myapp.domain.criteria.TrxProductHistoryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxProductHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxProductHistoryRepository extends ReactiveCrudRepository<TrxProductHistory, Long>, TrxProductHistoryRepositoryInternal {
    Flux<TrxProductHistory> findAllBy(Pageable pageable);

    @Override
    <S extends TrxProductHistory> Mono<S> save(S entity);

    @Override
    Flux<TrxProductHistory> findAll();

    @Override
    Mono<TrxProductHistory> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxProductHistoryRepositoryInternal {
    <S extends TrxProductHistory> Mono<S> save(S entity);

    Flux<TrxProductHistory> findAllBy(Pageable pageable);

    Flux<TrxProductHistory> findAll();

    Mono<TrxProductHistory> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxProductHistory> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxProductHistory> findByCriteria(TrxProductHistoryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxProductHistoryCriteria criteria);
}
