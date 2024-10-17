package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxNotification;
import com.mycompany.myapp.domain.criteria.TrxNotificationCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxNotification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxNotificationRepository extends ReactiveCrudRepository<TrxNotification, Long>, TrxNotificationRepositoryInternal {
    Flux<TrxNotification> findAllBy(Pageable pageable);

    @Query("SELECT * FROM trx_notification entity WHERE entity.customer_id = :id")
    Flux<TrxNotification> findByCustomer(Long id);

    @Query("SELECT * FROM trx_notification entity WHERE entity.customer_id IS NULL")
    Flux<TrxNotification> findAllWhereCustomerIsNull();

    @Override
    <S extends TrxNotification> Mono<S> save(S entity);

    @Override
    Flux<TrxNotification> findAll();

    @Override
    Mono<TrxNotification> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxNotificationRepositoryInternal {
    <S extends TrxNotification> Mono<S> save(S entity);

    Flux<TrxNotification> findAllBy(Pageable pageable);

    Flux<TrxNotification> findAll();

    Mono<TrxNotification> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxNotification> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxNotification> findByCriteria(TrxNotificationCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxNotificationCriteria criteria);
}
