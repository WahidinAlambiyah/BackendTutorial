package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstJob;
import com.mycompany.myapp.domain.criteria.MstJobCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstJob entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstJobRepository extends ReactiveCrudRepository<MstJob, Long>, MstJobRepositoryInternal {
    Flux<MstJob> findAllBy(Pageable pageable);

    @Override
    Mono<MstJob> findOneWithEagerRelationships(Long id);

    @Override
    Flux<MstJob> findAllWithEagerRelationships();

    @Override
    Flux<MstJob> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM mst_job entity JOIN rel_mst_job__task joinTable ON entity.id = joinTable.task_id WHERE joinTable.task_id = :id"
    )
    Flux<MstJob> findByTask(Long id);

    @Query("SELECT * FROM mst_job entity WHERE entity.employee_id = :id")
    Flux<MstJob> findByEmployee(Long id);

    @Query("SELECT * FROM mst_job entity WHERE entity.employee_id IS NULL")
    Flux<MstJob> findAllWhereEmployeeIsNull();

    @Query("SELECT * FROM mst_job entity WHERE entity.id not in (select job_history_id from job_history)")
    Flux<MstJob> findAllWhereJobHistoryIsNull();

    @Override
    <S extends MstJob> Mono<S> save(S entity);

    @Override
    Flux<MstJob> findAll();

    @Override
    Mono<MstJob> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstJobRepositoryInternal {
    <S extends MstJob> Mono<S> save(S entity);

    Flux<MstJob> findAllBy(Pageable pageable);

    Flux<MstJob> findAll();

    Mono<MstJob> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstJob> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstJob> findByCriteria(MstJobCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstJobCriteria criteria);

    Mono<MstJob> findOneWithEagerRelationships(Long id);

    Flux<MstJob> findAllWithEagerRelationships();

    Flux<MstJob> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
