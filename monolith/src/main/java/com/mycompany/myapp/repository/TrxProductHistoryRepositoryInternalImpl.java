package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxProductHistory;
import com.mycompany.myapp.domain.criteria.TrxProductHistoryCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.TrxProductHistoryRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxProductHistory entity.
 */
@SuppressWarnings("unused")
class TrxProductHistoryRepositoryInternalImpl
    extends SimpleR2dbcRepository<TrxProductHistory, Long>
    implements TrxProductHistoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxProductHistoryRowMapper trxproducthistoryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_product_history", EntityManager.ENTITY_ALIAS);

    public TrxProductHistoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxProductHistoryRowMapper trxproducthistoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxProductHistory.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxproducthistoryMapper = trxproducthistoryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxProductHistory> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxProductHistory> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxProductHistorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxProductHistory.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxProductHistory> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxProductHistory> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxProductHistory process(Row row, RowMetadata metadata) {
        TrxProductHistory entity = trxproducthistoryMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends TrxProductHistory> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxProductHistory> findByCriteria(TrxProductHistoryCriteria trxProductHistoryCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxProductHistoryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxProductHistoryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxProductHistoryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getOldPrice() != null) {
                builder.buildFilterConditionForField(criteria.getOldPrice(), entityTable.column("old_price"));
            }
            if (criteria.getNewPrice() != null) {
                builder.buildFilterConditionForField(criteria.getNewPrice(), entityTable.column("new_price"));
            }
            if (criteria.getChangeDate() != null) {
                builder.buildFilterConditionForField(criteria.getChangeDate(), entityTable.column("change_date"));
            }
        }
        return builder.buildConditions();
    }
}
