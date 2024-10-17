package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxDiscount;
import com.mycompany.myapp.domain.criteria.TrxDiscountCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.TrxDiscountRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxDiscount entity.
 */
@SuppressWarnings("unused")
class TrxDiscountRepositoryInternalImpl extends SimpleR2dbcRepository<TrxDiscount, Long> implements TrxDiscountRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxDiscountRowMapper trxdiscountMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_discount", EntityManager.ENTITY_ALIAS);

    public TrxDiscountRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxDiscountRowMapper trxdiscountMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxDiscount.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxdiscountMapper = trxdiscountMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxDiscount> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxDiscount> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxDiscountSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxDiscount.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxDiscount> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxDiscount> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxDiscount process(Row row, RowMetadata metadata) {
        TrxDiscount entity = trxdiscountMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends TrxDiscount> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxDiscount> findByCriteria(TrxDiscountCriteria trxDiscountCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxDiscountCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxDiscountCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxDiscountCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getDiscountPercentage() != null) {
                builder.buildFilterConditionForField(criteria.getDiscountPercentage(), entityTable.column("discount_percentage"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getEndDate() != null) {
                builder.buildFilterConditionForField(criteria.getEndDate(), entityTable.column("end_date"));
            }
        }
        return builder.buildConditions();
    }
}
