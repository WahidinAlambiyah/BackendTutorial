package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxOrderItem;
import com.mycompany.myapp.domain.criteria.TrxOrderItemCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstProductRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrxOrderItemRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrxOrderRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the TrxOrderItem entity.
 */
@SuppressWarnings("unused")
class TrxOrderItemRepositoryInternalImpl extends SimpleR2dbcRepository<TrxOrderItem, Long> implements TrxOrderItemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxOrderRowMapper trxorderMapper;
    private final MstProductRowMapper mstproductMapper;
    private final TrxOrderItemRowMapper trxorderitemMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_order_item", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("trx_order", "e_order");
    private static final Table productTable = Table.aliased("mst_product", "product");

    public TrxOrderItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxOrderRowMapper trxorderMapper,
        MstProductRowMapper mstproductMapper,
        TrxOrderItemRowMapper trxorderitemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxOrderItem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxorderMapper = trxorderMapper;
        this.mstproductMapper = mstproductMapper;
        this.trxorderitemMapper = trxorderitemMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxOrderItem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxOrderItem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxOrderItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TrxOrderSqlHelper.getColumns(orderTable, "order"));
        columns.addAll(MstProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxOrderItem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxOrderItem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxOrderItem> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxOrderItem process(Row row, RowMetadata metadata) {
        TrxOrderItem entity = trxorderitemMapper.apply(row, "e");
        entity.setOrder(trxorderMapper.apply(row, "order"));
        entity.setProduct(mstproductMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends TrxOrderItem> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxOrderItem> findByCriteria(TrxOrderItemCriteria trxOrderItemCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxOrderItemCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxOrderItemCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxOrderItemCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getQuantity() != null) {
                builder.buildFilterConditionForField(criteria.getQuantity(), entityTable.column("quantity"));
            }
            if (criteria.getPrice() != null) {
                builder.buildFilterConditionForField(criteria.getPrice(), entityTable.column("price"));
            }
            if (criteria.getOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getOrderId(), orderTable.column("id"));
            }
            if (criteria.getProductId() != null) {
                builder.buildFilterConditionForField(criteria.getProductId(), productTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
