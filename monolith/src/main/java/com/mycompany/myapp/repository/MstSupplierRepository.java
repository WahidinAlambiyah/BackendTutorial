package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstSupplier;
import com.mycompany.myapp.domain.criteria.MstSupplierCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstSupplier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstSupplierRepository extends ReactiveCrudRepository<MstSupplier, Long>, MstSupplierRepositoryInternal {
    Flux<MstSupplier> findAllBy(Pageable pageable);

    @Override
    <S extends MstSupplier> Mono<S> save(S entity);

    @Override
    Flux<MstSupplier> findAll();

    @Override
    Mono<MstSupplier> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstSupplierRepositoryInternal {
    <S extends MstSupplier> Mono<S> save(S entity);

    Flux<MstSupplier> findAllBy(Pageable pageable);

    Flux<MstSupplier> findAll();

    Mono<MstSupplier> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstSupplier> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstSupplier> findByCriteria(MstSupplierCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstSupplierCriteria criteria);
}
