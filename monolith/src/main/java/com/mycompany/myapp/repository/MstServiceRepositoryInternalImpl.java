package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstService;
import com.mycompany.myapp.domain.criteria.MstServiceCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstServiceRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstService entity.
 */
@SuppressWarnings("unused")
class MstServiceRepositoryInternalImpl extends SimpleR2dbcRepository<MstService, Long> implements MstServiceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxTestimonialRowMapper trxtestimonialMapper;
    private final MstServiceRowMapper mstserviceMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_service", EntityManager.ENTITY_ALIAS);
    private static final Table testimonialTable = Table.aliased("trx_testimonial", "testimonial");

    public MstServiceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxTestimonialRowMapper trxtestimonialMapper,
        MstServiceRowMapper mstserviceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstService.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxtestimonialMapper = trxtestimonialMapper;
        this.mstserviceMapper = mstserviceMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstService> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstService> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstServiceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TrxTestimonialSqlHelper.getColumns(testimonialTable, "testimonial"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(testimonialTable)
            .on(Column.create("testimonial_id", entityTable))
            .equals(Column.create("id", testimonialTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstService.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstService> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstService> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private MstService process(Row row, RowMetadata metadata) {
        MstService entity = mstserviceMapper.apply(row, "e");
        entity.setTestimonial(trxtestimonialMapper.apply(row, "testimonial"));
        return entity;
    }

    @Override
    public <S extends MstService> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstService> findByCriteria(MstServiceCriteria mstServiceCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstServiceCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstServiceCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstServiceCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getPrice() != null) {
                builder.buildFilterConditionForField(criteria.getPrice(), entityTable.column("price"));
            }
            if (criteria.getDurationInHours() != null) {
                builder.buildFilterConditionForField(criteria.getDurationInHours(), entityTable.column("duration_in_hours"));
            }
            if (criteria.getServiceType() != null) {
                builder.buildFilterConditionForField(criteria.getServiceType(), entityTable.column("service_type"));
            }
            if (criteria.getTestimonialId() != null) {
                builder.buildFilterConditionForField(criteria.getTestimonialId(), testimonialTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
