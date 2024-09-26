package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxTestimonial;
import com.mycompany.myapp.domain.criteria.TrxTestimonialCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.TrxTestimonialRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxTestimonial entity.
 */
@SuppressWarnings("unused")
class TrxTestimonialRepositoryInternalImpl extends SimpleR2dbcRepository<TrxTestimonial, Long> implements TrxTestimonialRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxTestimonialRowMapper trxtestimonialMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_testimonial", EntityManager.ENTITY_ALIAS);

    public TrxTestimonialRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxTestimonialRowMapper trxtestimonialMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxTestimonial.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxtestimonialMapper = trxtestimonialMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxTestimonial> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxTestimonial> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxTestimonialSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxTestimonial.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxTestimonial> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxTestimonial> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxTestimonial process(Row row, RowMetadata metadata) {
        TrxTestimonial entity = trxtestimonialMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends TrxTestimonial> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxTestimonial> findByCriteria(TrxTestimonialCriteria trxTestimonialCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxTestimonialCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxTestimonialCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxTestimonialCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getRating() != null) {
                builder.buildFilterConditionForField(criteria.getRating(), entityTable.column("rating"));
            }
            if (criteria.getDate() != null) {
                builder.buildFilterConditionForField(criteria.getDate(), entityTable.column("date"));
            }
        }
        return builder.buildConditions();
    }
}
