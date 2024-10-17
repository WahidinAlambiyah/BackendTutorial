package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstDepartment;
import com.mycompany.myapp.domain.criteria.MstDepartmentCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstDepartment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstDepartmentRepository extends ReactiveCrudRepository<MstDepartment, Long>, MstDepartmentRepositoryInternal {
    Flux<MstDepartment> findAllBy(Pageable pageable);

    @Query("SELECT * FROM mst_department entity WHERE entity.location_id = :id")
    Flux<MstDepartment> findByLocation(Long id);

    @Query("SELECT * FROM mst_department entity WHERE entity.location_id IS NULL")
    Flux<MstDepartment> findAllWhereLocationIsNull();

    @Query("SELECT * FROM mst_department entity WHERE entity.id not in (select job_history_id from job_history)")
    Flux<MstDepartment> findAllWhereJobHistoryIsNull();

    @Override
    <S extends MstDepartment> Mono<S> save(S entity);

    @Override
    Flux<MstDepartment> findAll();

    @Override
    Mono<MstDepartment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstDepartmentRepositoryInternal {
    <S extends MstDepartment> Mono<S> save(S entity);

    Flux<MstDepartment> findAllBy(Pageable pageable);

    Flux<MstDepartment> findAll();

    Mono<MstDepartment> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstDepartment> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstDepartment> findByCriteria(MstDepartmentCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstDepartmentCriteria criteria);
}
