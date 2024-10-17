package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxStockAlert;
import com.mycompany.myapp.domain.criteria.TrxStockAlertCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxStockAlert entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxStockAlertRepository extends ReactiveCrudRepository<TrxStockAlert, Long>, TrxStockAlertRepositoryInternal {
    Flux<TrxStockAlert> findAllBy(Pageable pageable);

    @Override
    <S extends TrxStockAlert> Mono<S> save(S entity);

    @Override
    Flux<TrxStockAlert> findAll();

    @Override
    Mono<TrxStockAlert> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxStockAlertRepositoryInternal {
    <S extends TrxStockAlert> Mono<S> save(S entity);

    Flux<TrxStockAlert> findAllBy(Pageable pageable);

    Flux<TrxStockAlert> findAll();

    Mono<TrxStockAlert> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxStockAlert> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxStockAlert> findByCriteria(TrxStockAlertCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxStockAlertCriteria criteria);
}
