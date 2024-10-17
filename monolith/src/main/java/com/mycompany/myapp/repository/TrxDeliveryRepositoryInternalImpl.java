package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxDelivery;
import com.mycompany.myapp.domain.criteria.TrxDeliveryCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstDriverRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrxDeliveryRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxDelivery entity.
 */
@SuppressWarnings("unused")
class TrxDeliveryRepositoryInternalImpl extends SimpleR2dbcRepository<TrxDelivery, Long> implements TrxDeliveryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstDriverRowMapper mstdriverMapper;
    private final TrxOrderRowMapper trxorderMapper;
    private final TrxDeliveryRowMapper trxdeliveryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_delivery", EntityManager.ENTITY_ALIAS);
    private static final Table driverTable = Table.aliased("mst_driver", "driver");
    private static final Table trxOrderTable = Table.aliased("trx_order", "trxOrder");

    public TrxDeliveryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstDriverRowMapper mstdriverMapper,
        TrxOrderRowMapper trxorderMapper,
        TrxDeliveryRowMapper trxdeliveryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxDelivery.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstdriverMapper = mstdriverMapper;
        this.trxorderMapper = trxorderMapper;
        this.trxdeliveryMapper = trxdeliveryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxDelivery> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxDelivery> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxDeliverySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstDriverSqlHelper.getColumns(driverTable, "driver"));
        columns.addAll(TrxOrderSqlHelper.getColumns(trxOrderTable, "trxOrder"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(driverTable)
            .on(Column.create("driver_id", entityTable))
            .equals(Column.create("id", driverTable))
            .leftOuterJoin(trxOrderTable)
            .on(Column.create("trx_order_id", entityTable))
            .equals(Column.create("id", trxOrderTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxDelivery.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxDelivery> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxDelivery> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxDelivery process(Row row, RowMetadata metadata) {
        TrxDelivery entity = trxdeliveryMapper.apply(row, "e");
        entity.setDriver(mstdriverMapper.apply(row, "driver"));
        entity.setTrxOrder(trxorderMapper.apply(row, "trxOrder"));
        return entity;
    }

    @Override
    public <S extends TrxDelivery> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxDelivery> findByCriteria(TrxDeliveryCriteria trxDeliveryCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxDeliveryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxDeliveryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxDeliveryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getDeliveryAddress() != null) {
                builder.buildFilterConditionForField(criteria.getDeliveryAddress(), entityTable.column("delivery_address"));
            }
            if (criteria.getDeliveryStatus() != null) {
                builder.buildFilterConditionForField(criteria.getDeliveryStatus(), entityTable.column("delivery_status"));
            }
            if (criteria.getAssignedDriver() != null) {
                builder.buildFilterConditionForField(criteria.getAssignedDriver(), entityTable.column("assigned_driver"));
            }
            if (criteria.getEstimatedDeliveryTime() != null) {
                builder.buildFilterConditionForField(criteria.getEstimatedDeliveryTime(), entityTable.column("estimated_delivery_time"));
            }
            if (criteria.getDriverId() != null) {
                builder.buildFilterConditionForField(criteria.getDriverId(), driverTable.column("id"));
            }
            if (criteria.getTrxOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getTrxOrderId(), trxOrderTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
