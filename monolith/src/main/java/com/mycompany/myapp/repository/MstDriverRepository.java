package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstDriver;
import com.mycompany.myapp.domain.criteria.MstDriverCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstDriver entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstDriverRepository extends ReactiveCrudRepository<MstDriver, Long>, MstDriverRepositoryInternal {
    Flux<MstDriver> findAllBy(Pageable pageable);

    @Override
    <S extends MstDriver> Mono<S> save(S entity);

    @Override
    Flux<MstDriver> findAll();

    @Override
    Mono<MstDriver> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstDriverRepositoryInternal {
    <S extends MstDriver> Mono<S> save(S entity);

    Flux<MstDriver> findAllBy(Pageable pageable);

    Flux<MstDriver> findAll();

    Mono<MstDriver> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstDriver> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstDriver> findByCriteria(MstDriverCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstDriverCriteria criteria);
}
