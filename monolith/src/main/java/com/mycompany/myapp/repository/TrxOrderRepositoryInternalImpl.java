package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.domain.criteria.TrxOrderCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCustomerRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxOrder entity.
 */
@SuppressWarnings("unused")
class TrxOrderRepositoryInternalImpl extends SimpleR2dbcRepository<TrxOrder, Long> implements TrxOrderRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstCustomerRowMapper mstcustomerMapper;
    private final TrxOrderRowMapper trxorderMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_order", EntityManager.ENTITY_ALIAS);
    private static final Table mstCustomerTable = Table.aliased("mst_customer", "mstCustomer");

    public TrxOrderRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstCustomerRowMapper mstcustomerMapper,
        TrxOrderRowMapper trxorderMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxOrder.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstcustomerMapper = mstcustomerMapper;
        this.trxorderMapper = trxorderMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxOrder> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxOrder> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxOrderSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstCustomerSqlHelper.getColumns(mstCustomerTable, "mstCustomer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(mstCustomerTable)
            .on(Column.create("mst_customer_id", entityTable))
            .equals(Column.create("id", mstCustomerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxOrder.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxOrder> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxOrder> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxOrder process(Row row, RowMetadata metadata) {
        TrxOrder entity = trxorderMapper.apply(row, "e");
        entity.setMstCustomer(mstcustomerMapper.apply(row, "mstCustomer"));
        return entity;
    }

    @Override
    public <S extends TrxOrder> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxOrder> findByCriteria(TrxOrderCriteria trxOrderCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxOrderCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxOrderCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxOrderCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getOrderDate() != null) {
                builder.buildFilterConditionForField(criteria.getOrderDate(), entityTable.column("order_date"));
            }
            if (criteria.getDeliveryDate() != null) {
                builder.buildFilterConditionForField(criteria.getDeliveryDate(), entityTable.column("delivery_date"));
            }
            if (criteria.getOrderStatus() != null) {
                builder.buildFilterConditionForField(criteria.getOrderStatus(), entityTable.column("order_status"));
            }
            if (criteria.getPaymentMethod() != null) {
                builder.buildFilterConditionForField(criteria.getPaymentMethod(), entityTable.column("payment_method"));
            }
            if (criteria.getTotalAmount() != null) {
                builder.buildFilterConditionForField(criteria.getTotalAmount(), entityTable.column("total_amount"));
            }
            if (criteria.getMstCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getMstCustomerId(), mstCustomerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
