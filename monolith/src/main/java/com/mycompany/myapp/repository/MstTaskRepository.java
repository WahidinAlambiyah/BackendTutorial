package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstTask;
import com.mycompany.myapp.domain.criteria.MstTaskCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstTaskRepository extends ReactiveCrudRepository<MstTask, Long>, MstTaskRepositoryInternal {
    Flux<MstTask> findAllBy(Pageable pageable);

    @Override
    <S extends MstTask> Mono<S> save(S entity);

    @Override
    Flux<MstTask> findAll();

    @Override
    Mono<MstTask> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstTaskRepositoryInternal {
    <S extends MstTask> Mono<S> save(S entity);

    Flux<MstTask> findAllBy(Pageable pageable);

    Flux<MstTask> findAll();

    Mono<MstTask> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstTask> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstTask> findByCriteria(MstTaskCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstTaskCriteria criteria);
}
