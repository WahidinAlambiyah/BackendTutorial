package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Stock;
import com.mycompany.myapp.domain.criteria.StockCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Stock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockRepository extends ReactiveCrudRepository<Stock, Long>, StockRepositoryInternal {
    Flux<Stock> findAllBy(Pageable pageable);

    @Query("SELECT * FROM stock entity WHERE entity.product_id = :id")
    Flux<Stock> findByProduct(Long id);

    @Query("SELECT * FROM stock entity WHERE entity.product_id IS NULL")
    Flux<Stock> findAllWhereProductIsNull();

    @Override
    <S extends Stock> Mono<S> save(S entity);

    @Override
    Flux<Stock> findAll();

    @Override
    Mono<Stock> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StockRepositoryInternal {
    <S extends Stock> Mono<S> save(S entity);

    Flux<Stock> findAllBy(Pageable pageable);

    Flux<Stock> findAll();

    Mono<Stock> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Stock> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Stock> findByCriteria(StockCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(StockCriteria criteria);
}
