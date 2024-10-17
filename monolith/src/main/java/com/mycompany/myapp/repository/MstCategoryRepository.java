package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstCategory;
import com.mycompany.myapp.domain.criteria.MstCategoryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstCategoryRepository extends ReactiveCrudRepository<MstCategory, Long>, MstCategoryRepositoryInternal {
    Flux<MstCategory> findAllBy(Pageable pageable);

    @Override
    <S extends MstCategory> Mono<S> save(S entity);

    @Override
    Flux<MstCategory> findAll();

    @Override
    Mono<MstCategory> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstCategoryRepositoryInternal {
    <S extends MstCategory> Mono<S> save(S entity);

    Flux<MstCategory> findAllBy(Pageable pageable);

    Flux<MstCategory> findAll();

    Mono<MstCategory> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstCategory> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstCategory> findByCriteria(MstCategoryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstCategoryCriteria criteria);
}
