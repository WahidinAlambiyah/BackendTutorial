package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstBrand;
import com.mycompany.myapp.domain.criteria.MstBrandCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstBrand entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstBrandRepository extends ReactiveCrudRepository<MstBrand, Long>, MstBrandRepositoryInternal {
    Flux<MstBrand> findAllBy(Pageable pageable);

    @Override
    <S extends MstBrand> Mono<S> save(S entity);

    @Override
    Flux<MstBrand> findAll();

    @Override
    Mono<MstBrand> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstBrandRepositoryInternal {
    <S extends MstBrand> Mono<S> save(S entity);

    Flux<MstBrand> findAllBy(Pageable pageable);

    Flux<MstBrand> findAll();

    Mono<MstBrand> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstBrand> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstBrand> findByCriteria(MstBrandCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstBrandCriteria criteria);
}
