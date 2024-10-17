package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.criteria.MstCustomerCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstCustomer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstCustomerRepository extends ReactiveCrudRepository<MstCustomer, Long>, MstCustomerRepositoryInternal {
    Flux<MstCustomer> findAllBy(Pageable pageable);

    @Override
    <S extends MstCustomer> Mono<S> save(S entity);

    @Override
    Flux<MstCustomer> findAll();

    @Override
    Mono<MstCustomer> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstCustomerRepositoryInternal {
    <S extends MstCustomer> Mono<S> save(S entity);

    Flux<MstCustomer> findAllBy(Pageable pageable);

    Flux<MstCustomer> findAll();

    Mono<MstCustomer> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstCustomer> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstCustomer> findByCriteria(MstCustomerCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstCustomerCriteria criteria);
}
