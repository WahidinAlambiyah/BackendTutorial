package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, Long>, EventRepositoryInternal {
    @Query("SELECT * FROM event entity WHERE entity.service_id = :id")
    Flux<Event> findByService(Long id);

    @Query("SELECT * FROM event entity WHERE entity.service_id IS NULL")
    Flux<Event> findAllWhereServiceIsNull();

    @Query("SELECT * FROM event entity WHERE entity.testimonial_id = :id")
    Flux<Event> findByTestimonial(Long id);

    @Query("SELECT * FROM event entity WHERE entity.testimonial_id IS NULL")
    Flux<Event> findAllWhereTestimonialIsNull();

    @Override
    <S extends Event> Mono<S> save(S entity);

    @Override
    Flux<Event> findAll();

    @Override
    Mono<Event> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EventRepositoryInternal {
    <S extends Event> Mono<S> save(S entity);

    Flux<Event> findAllBy(Pageable pageable);

    Flux<Event> findAll();

    Mono<Event> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Event> findAllBy(Pageable pageable, Criteria criteria);
}
