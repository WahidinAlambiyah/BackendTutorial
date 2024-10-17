package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxCoupon;
import com.mycompany.myapp.domain.criteria.TrxCouponCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.TrxCouponRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the TrxCoupon entity.
 */
@SuppressWarnings("unused")
class TrxCouponRepositoryInternalImpl extends SimpleR2dbcRepository<TrxCoupon, Long> implements TrxCouponRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxCouponRowMapper trxcouponMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_coupon", EntityManager.ENTITY_ALIAS);

    public TrxCouponRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxCouponRowMapper trxcouponMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxCoupon.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxcouponMapper = trxcouponMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxCoupon> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxCoupon> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxCouponSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxCoupon.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxCoupon> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxCoupon> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxCoupon process(Row row, RowMetadata metadata) {
        TrxCoupon entity = trxcouponMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends TrxCoupon> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxCoupon> findByCriteria(TrxCouponCriteria trxCouponCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxCouponCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxCouponCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxCouponCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getCode() != null) {
                builder.buildFilterConditionForField(criteria.getCode(), entityTable.column("code"));
            }
            if (criteria.getDiscountAmount() != null) {
                builder.buildFilterConditionForField(criteria.getDiscountAmount(), entityTable.column("discount_amount"));
            }
            if (criteria.getValidUntil() != null) {
                builder.buildFilterConditionForField(criteria.getValidUntil(), entityTable.column("valid_until"));
            }
            if (criteria.getMinPurchase() != null) {
                builder.buildFilterConditionForField(criteria.getMinPurchase(), entityTable.column("min_purchase"));
            }
        }
        return builder.buildConditions();
    }
}
