package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstLoyaltyProgram;
import com.mycompany.myapp.domain.criteria.MstLoyaltyProgramCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstLoyaltyProgram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstLoyaltyProgramRepository extends ReactiveCrudRepository<MstLoyaltyProgram, Long>, MstLoyaltyProgramRepositoryInternal {
    Flux<MstLoyaltyProgram> findAllBy(Pageable pageable);

    @Query("SELECT * FROM mst_loyalty_program entity WHERE entity.customer_id = :id")
    Flux<MstLoyaltyProgram> findByCustomer(Long id);

    @Query("SELECT * FROM mst_loyalty_program entity WHERE entity.customer_id IS NULL")
    Flux<MstLoyaltyProgram> findAllWhereCustomerIsNull();

    @Override
    <S extends MstLoyaltyProgram> Mono<S> save(S entity);

    @Override
    Flux<MstLoyaltyProgram> findAll();

    @Override
    Mono<MstLoyaltyProgram> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstLoyaltyProgramRepositoryInternal {
    <S extends MstLoyaltyProgram> Mono<S> save(S entity);

    Flux<MstLoyaltyProgram> findAllBy(Pageable pageable);

    Flux<MstLoyaltyProgram> findAll();

    Mono<MstLoyaltyProgram> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstLoyaltyProgram> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstLoyaltyProgram> findByCriteria(MstLoyaltyProgramCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstLoyaltyProgramCriteria criteria);
}
