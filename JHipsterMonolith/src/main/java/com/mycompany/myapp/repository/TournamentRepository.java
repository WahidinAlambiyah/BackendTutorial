package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tournament;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Tournament entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TournamentRepository extends ReactiveCrudRepository<Tournament, Long>, TournamentRepositoryInternal {
    @Override
    Mono<Tournament> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Tournament> findAllWithEagerRelationships();

    @Override
    Flux<Tournament> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM tournament entity WHERE entity.event_id = :id")
    Flux<Tournament> findByEvent(Long id);

    @Query("SELECT * FROM tournament entity WHERE entity.event_id IS NULL")
    Flux<Tournament> findAllWhereEventIsNull();

    @Override
    <S extends Tournament> Mono<S> save(S entity);

    @Override
    Flux<Tournament> findAll();

    @Override
    Mono<Tournament> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TournamentRepositoryInternal {
    <S extends Tournament> Mono<S> save(S entity);

    Flux<Tournament> findAllBy(Pageable pageable);

    Flux<Tournament> findAll();

    Mono<Tournament> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tournament> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Tournament> findOneWithEagerRelationships(Long id);

    Flux<Tournament> findAllWithEagerRelationships();

    Flux<Tournament> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
