package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxTournament;
import com.mycompany.myapp.domain.criteria.TrxTournamentCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TrxTournament entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrxTournamentRepository extends ReactiveCrudRepository<TrxTournament, Long>, TrxTournamentRepositoryInternal {
    @Override
    Mono<TrxTournament> findOneWithEagerRelationships(Long id);

    @Override
    Flux<TrxTournament> findAllWithEagerRelationships();

    @Override
    Flux<TrxTournament> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM trx_tournament entity WHERE entity.event_id = :id")
    Flux<TrxTournament> findByEvent(Long id);

    @Query("SELECT * FROM trx_tournament entity WHERE entity.event_id IS NULL")
    Flux<TrxTournament> findAllWhereEventIsNull();

    @Override
    <S extends TrxTournament> Mono<S> save(S entity);

    @Override
    Flux<TrxTournament> findAll();

    @Override
    Mono<TrxTournament> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrxTournamentRepositoryInternal {
    <S extends TrxTournament> Mono<S> save(S entity);

    Flux<TrxTournament> findAllBy(Pageable pageable);

    Flux<TrxTournament> findAll();

    Mono<TrxTournament> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TrxTournament> findAllBy(Pageable pageable, Criteria criteria);
    Flux<TrxTournament> findByCriteria(TrxTournamentCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TrxTournamentCriteria criteria);

    Mono<TrxTournament> findOneWithEagerRelationships(Long id);

    Flux<TrxTournament> findAllWithEagerRelationships();

    Flux<TrxTournament> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
