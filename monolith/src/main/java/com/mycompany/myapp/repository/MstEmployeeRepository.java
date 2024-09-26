package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstEmployee;
import com.mycompany.myapp.domain.criteria.MstEmployeeCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstEmployee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstEmployeeRepository extends ReactiveCrudRepository<MstEmployee, Long>, MstEmployeeRepositoryInternal {
    Flux<MstEmployee> findAllBy(Pageable pageable);

    @Query("SELECT * FROM mst_employee entity WHERE entity.manager_id = :id")
    Flux<MstEmployee> findByManager(Long id);

    @Query("SELECT * FROM mst_employee entity WHERE entity.manager_id IS NULL")
    Flux<MstEmployee> findAllWhereManagerIsNull();

    @Query("SELECT * FROM mst_employee entity WHERE entity.department_id = :id")
    Flux<MstEmployee> findByDepartment(Long id);

    @Query("SELECT * FROM mst_employee entity WHERE entity.department_id IS NULL")
    Flux<MstEmployee> findAllWhereDepartmentIsNull();

    @Query("SELECT * FROM mst_employee entity WHERE entity.id not in (select job_history_id from job_history)")
    Flux<MstEmployee> findAllWhereJobHistoryIsNull();

    @Override
    <S extends MstEmployee> Mono<S> save(S entity);

    @Override
    Flux<MstEmployee> findAll();

    @Override
    Mono<MstEmployee> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstEmployeeRepositoryInternal {
    <S extends MstEmployee> Mono<S> save(S entity);

    Flux<MstEmployee> findAllBy(Pageable pageable);

    Flux<MstEmployee> findAll();

    Mono<MstEmployee> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstEmployee> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstEmployee> findByCriteria(MstEmployeeCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstEmployeeCriteria criteria);
}
