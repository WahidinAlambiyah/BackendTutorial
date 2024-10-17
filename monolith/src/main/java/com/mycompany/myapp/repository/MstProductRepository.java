package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstProduct;
import com.mycompany.myapp.domain.criteria.MstProductCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MstProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MstProductRepository extends ReactiveCrudRepository<MstProduct, Long>, MstProductRepositoryInternal {
    Flux<MstProduct> findAllBy(Pageable pageable);

    @Query("SELECT * FROM mst_product entity WHERE entity.category_id = :id")
    Flux<MstProduct> findByCategory(Long id);

    @Query("SELECT * FROM mst_product entity WHERE entity.category_id IS NULL")
    Flux<MstProduct> findAllWhereCategoryIsNull();

    @Query("SELECT * FROM mst_product entity WHERE entity.brand_id = :id")
    Flux<MstProduct> findByBrand(Long id);

    @Query("SELECT * FROM mst_product entity WHERE entity.brand_id IS NULL")
    Flux<MstProduct> findAllWhereBrandIsNull();

    @Query("SELECT * FROM mst_product entity WHERE entity.mst_supplier_id = :id")
    Flux<MstProduct> findByMstSupplier(Long id);

    @Query("SELECT * FROM mst_product entity WHERE entity.mst_supplier_id IS NULL")
    Flux<MstProduct> findAllWhereMstSupplierIsNull();

    @Override
    <S extends MstProduct> Mono<S> save(S entity);

    @Override
    Flux<MstProduct> findAll();

    @Override
    Mono<MstProduct> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MstProductRepositoryInternal {
    <S extends MstProduct> Mono<S> save(S entity);

    Flux<MstProduct> findAllBy(Pageable pageable);

    Flux<MstProduct> findAll();

    Mono<MstProduct> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MstProduct> findAllBy(Pageable pageable, Criteria criteria);
    Flux<MstProduct> findByCriteria(MstProductCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MstProductCriteria criteria);
}
