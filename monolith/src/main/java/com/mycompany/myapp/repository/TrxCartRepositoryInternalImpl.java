package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxCart;
import com.mycompany.myapp.domain.criteria.TrxCartCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCustomerRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrxCartRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxCart entity.
 */
@SuppressWarnings("unused")
class TrxCartRepositoryInternalImpl extends SimpleR2dbcRepository<TrxCart, Long> implements TrxCartRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstCustomerRowMapper mstcustomerMapper;
    private final TrxCartRowMapper trxcartMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_cart", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("mst_customer", "customer");

    public TrxCartRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstCustomerRowMapper mstcustomerMapper,
        TrxCartRowMapper trxcartMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxCart.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstcustomerMapper = mstcustomerMapper;
        this.trxcartMapper = trxcartMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxCart> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxCart> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxCartSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstCustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxCart.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxCart> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxCart> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxCart process(Row row, RowMetadata metadata) {
        TrxCart entity = trxcartMapper.apply(row, "e");
        entity.setCustomer(mstcustomerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends TrxCart> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxCart> findByCriteria(TrxCartCriteria trxCartCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxCartCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxCartCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxCartCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getTotalPrice() != null) {
                builder.buildFilterConditionForField(criteria.getTotalPrice(), entityTable.column("total_price"));
            }
            if (criteria.getCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getCustomerId(), customerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
