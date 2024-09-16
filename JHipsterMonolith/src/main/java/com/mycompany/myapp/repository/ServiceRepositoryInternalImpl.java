package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Services;
import com.mycompany.myapp.repository.rowmapper.ServiceRowMapper;
import com.mycompany.myapp.repository.rowmapper.TestimonialRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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

/**
 * Spring Data R2DBC custom repository implementation for the Service entity.
 */
@SuppressWarnings("unused")
class ServiceRepositoryInternalImpl extends SimpleR2dbcRepository<Services, Long> implements ServiceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TestimonialRowMapper testimonialMapper;
    private final ServiceRowMapper serviceMapper;

    private static final Table entityTable = Table.aliased("service", EntityManager.ENTITY_ALIAS);
    private static final Table testimonialTable = Table.aliased("testimonial", "testimonial");

    public ServiceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TestimonialRowMapper testimonialMapper,
        ServiceRowMapper serviceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Services.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.testimonialMapper = testimonialMapper;
        this.serviceMapper = serviceMapper;
    }

    @Override
    public Flux<Services> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Services> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ServiceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TestimonialSqlHelper.getColumns(testimonialTable, "testimonial"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(testimonialTable)
            .on(Column.create("testimonial_id", entityTable))
            .equals(Column.create("id", testimonialTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Services.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Services> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Services> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Services process(Row row, RowMetadata metadata) {
        Services entity = serviceMapper.apply(row, "e");
        entity.setTestimonial(testimonialMapper.apply(row, "testimonial"));
        return entity;
    }

    @Override
    public <S extends Services> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
