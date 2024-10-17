package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxOrderStock;
import com.mycompany.myapp.domain.criteria.TrxOrderStockCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstSupplierRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrxOrderStockRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxOrderStock entity.
 */
@SuppressWarnings("unused")
class TrxOrderStockRepositoryInternalImpl extends SimpleR2dbcRepository<TrxOrderStock, Long> implements TrxOrderStockRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstSupplierRowMapper mstsupplierMapper;
    private final TrxOrderStockRowMapper trxorderstockMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_order_stock", EntityManager.ENTITY_ALIAS);
    private static final Table supplierTable = Table.aliased("mst_supplier", "supplier");

    public TrxOrderStockRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstSupplierRowMapper mstsupplierMapper,
        TrxOrderStockRowMapper trxorderstockMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxOrderStock.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstsupplierMapper = mstsupplierMapper;
        this.trxorderstockMapper = trxorderstockMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxOrderStock> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxOrderStock> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxOrderStockSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstSupplierSqlHelper.getColumns(supplierTable, "supplier"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(supplierTable)
            .on(Column.create("supplier_id", entityTable))
            .equals(Column.create("id", supplierTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxOrderStock.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxOrderStock> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxOrderStock> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxOrderStock process(Row row, RowMetadata metadata) {
        TrxOrderStock entity = trxorderstockMapper.apply(row, "e");
        entity.setSupplier(mstsupplierMapper.apply(row, "supplier"));
        return entity;
    }

    @Override
    public <S extends TrxOrderStock> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxOrderStock> findByCriteria(TrxOrderStockCriteria trxOrderStockCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxOrderStockCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxOrderStockCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxOrderStockCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getQuantityOrdered() != null) {
                builder.buildFilterConditionForField(criteria.getQuantityOrdered(), entityTable.column("quantity_ordered"));
            }
            if (criteria.getOrderDate() != null) {
                builder.buildFilterConditionForField(criteria.getOrderDate(), entityTable.column("order_date"));
            }
            if (criteria.getExpectedArrivalDate() != null) {
                builder.buildFilterConditionForField(criteria.getExpectedArrivalDate(), entityTable.column("expected_arrival_date"));
            }
            if (criteria.getSupplierId() != null) {
                builder.buildFilterConditionForField(criteria.getSupplierId(), supplierTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
