package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxEvent;
import com.mycompany.myapp.domain.criteria.TrxEventCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstServiceRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrxEventRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxEvent entity.
 */
@SuppressWarnings("unused")
class TrxEventRepositoryInternalImpl extends SimpleR2dbcRepository<TrxEvent, Long> implements TrxEventRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstServiceRowMapper mstserviceMapper;
    private final TrxTestimonialRowMapper trxtestimonialMapper;
    private final TrxEventRowMapper trxeventMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_event", EntityManager.ENTITY_ALIAS);
    private static final Table serviceTable = Table.aliased("mst_service", "service");
    private static final Table testimonialTable = Table.aliased("trx_testimonial", "testimonial");

    public TrxEventRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstServiceRowMapper mstserviceMapper,
        TrxTestimonialRowMapper trxtestimonialMapper,
        TrxEventRowMapper trxeventMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxEvent.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstserviceMapper = mstserviceMapper;
        this.trxtestimonialMapper = trxtestimonialMapper;
        this.trxeventMapper = trxeventMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxEvent> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxEvent> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxEventSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstServiceSqlHelper.getColumns(serviceTable, "service"));
        columns.addAll(TrxTestimonialSqlHelper.getColumns(testimonialTable, "testimonial"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(serviceTable)
            .on(Column.create("service_id", entityTable))
            .equals(Column.create("id", serviceTable))
            .leftOuterJoin(testimonialTable)
            .on(Column.create("testimonial_id", entityTable))
            .equals(Column.create("id", testimonialTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxEvent.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxEvent> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxEvent> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxEvent process(Row row, RowMetadata metadata) {
        TrxEvent entity = trxeventMapper.apply(row, "e");
        entity.setService(mstserviceMapper.apply(row, "service"));
        entity.setTestimonial(trxtestimonialMapper.apply(row, "testimonial"));
        return entity;
    }

    @Override
    public <S extends TrxEvent> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxEvent> findByCriteria(TrxEventCriteria trxEventCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxEventCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxEventCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxEventCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getTitle() != null) {
                builder.buildFilterConditionForField(criteria.getTitle(), entityTable.column("title"));
            }
            if (criteria.getDate() != null) {
                builder.buildFilterConditionForField(criteria.getDate(), entityTable.column("date"));
            }
            if (criteria.getLocation() != null) {
                builder.buildFilterConditionForField(criteria.getLocation(), entityTable.column("location"));
            }
            if (criteria.getCapacity() != null) {
                builder.buildFilterConditionForField(criteria.getCapacity(), entityTable.column("capacity"));
            }
            if (criteria.getPrice() != null) {
                builder.buildFilterConditionForField(criteria.getPrice(), entityTable.column("price"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getServiceId() != null) {
                builder.buildFilterConditionForField(criteria.getServiceId(), serviceTable.column("id"));
            }
            if (criteria.getTestimonialId() != null) {
                builder.buildFilterConditionForField(criteria.getTestimonialId(), testimonialTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
