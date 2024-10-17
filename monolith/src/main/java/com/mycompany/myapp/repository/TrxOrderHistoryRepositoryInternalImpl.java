package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxOrderHistory;
import com.mycompany.myapp.domain.criteria.TrxOrderHistoryCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.TrxOrderHistoryRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxOrderHistory entity.
 */
@SuppressWarnings("unused")
class TrxOrderHistoryRepositoryInternalImpl
    extends SimpleR2dbcRepository<TrxOrderHistory, Long>
    implements TrxOrderHistoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxOrderHistoryRowMapper trxorderhistoryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_order_history", EntityManager.ENTITY_ALIAS);

    public TrxOrderHistoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxOrderHistoryRowMapper trxorderhistoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxOrderHistory.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxorderhistoryMapper = trxorderhistoryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxOrderHistory> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxOrderHistory> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxOrderHistorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxOrderHistory.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxOrderHistory> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxOrderHistory> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxOrderHistory process(Row row, RowMetadata metadata) {
        TrxOrderHistory entity = trxorderhistoryMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends TrxOrderHistory> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxOrderHistory> findByCriteria(TrxOrderHistoryCriteria trxOrderHistoryCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxOrderHistoryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxOrderHistoryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxOrderHistoryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getPreviousStatus() != null) {
                builder.buildFilterConditionForField(criteria.getPreviousStatus(), entityTable.column("previous_status"));
            }
            if (criteria.getNewStatus() != null) {
                builder.buildFilterConditionForField(criteria.getNewStatus(), entityTable.column("new_status"));
            }
            if (criteria.getChangeDate() != null) {
                builder.buildFilterConditionForField(criteria.getChangeDate(), entityTable.column("change_date"));
            }
        }
        return builder.buildConditions();
    }
}
