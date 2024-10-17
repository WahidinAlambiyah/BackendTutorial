package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxStockAlert;
import com.mycompany.myapp.domain.criteria.TrxStockAlertCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.TrxStockAlertRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxStockAlert entity.
 */
@SuppressWarnings("unused")
class TrxStockAlertRepositoryInternalImpl extends SimpleR2dbcRepository<TrxStockAlert, Long> implements TrxStockAlertRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxStockAlertRowMapper trxstockalertMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_stock_alert", EntityManager.ENTITY_ALIAS);

    public TrxStockAlertRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxStockAlertRowMapper trxstockalertMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxStockAlert.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxstockalertMapper = trxstockalertMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxStockAlert> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxStockAlert> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxStockAlertSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxStockAlert.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxStockAlert> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxStockAlert> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxStockAlert process(Row row, RowMetadata metadata) {
        TrxStockAlert entity = trxstockalertMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends TrxStockAlert> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxStockAlert> findByCriteria(TrxStockAlertCriteria trxStockAlertCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxStockAlertCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxStockAlertCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxStockAlertCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getAlertThreshold() != null) {
                builder.buildFilterConditionForField(criteria.getAlertThreshold(), entityTable.column("alert_threshold"));
            }
            if (criteria.getCurrentStock() != null) {
                builder.buildFilterConditionForField(criteria.getCurrentStock(), entityTable.column("current_stock"));
            }
        }
        return builder.buildConditions();
    }
}
