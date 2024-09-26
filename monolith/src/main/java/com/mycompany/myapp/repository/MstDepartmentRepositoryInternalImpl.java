package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstDepartment;
import com.mycompany.myapp.domain.criteria.MstDepartmentCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.LocationRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstDepartmentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstDepartment entity.
 */
@SuppressWarnings("unused")
class MstDepartmentRepositoryInternalImpl extends SimpleR2dbcRepository<MstDepartment, Long> implements MstDepartmentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final LocationRowMapper locationMapper;
    private final MstDepartmentRowMapper mstdepartmentMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_department", EntityManager.ENTITY_ALIAS);
    private static final Table locationTable = Table.aliased("location", "location");

    public MstDepartmentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        LocationRowMapper locationMapper,
        MstDepartmentRowMapper mstdepartmentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstDepartment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.locationMapper = locationMapper;
        this.mstdepartmentMapper = mstdepartmentMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstDepartment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstDepartment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstDepartmentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(LocationSqlHelper.getColumns(locationTable, "location"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(locationTable)
            .on(Column.create("location_id", entityTable))
            .equals(Column.create("id", locationTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstDepartment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstDepartment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstDepartment> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private MstDepartment process(Row row, RowMetadata metadata) {
        MstDepartment entity = mstdepartmentMapper.apply(row, "e");
        entity.setLocation(locationMapper.apply(row, "location"));
        return entity;
    }

    @Override
    public <S extends MstDepartment> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstDepartment> findByCriteria(MstDepartmentCriteria mstDepartmentCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstDepartmentCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstDepartmentCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstDepartmentCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getDepartmentName() != null) {
                builder.buildFilterConditionForField(criteria.getDepartmentName(), entityTable.column("department_name"));
            }
            if (criteria.getLocationId() != null) {
                builder.buildFilterConditionForField(criteria.getLocationId(), locationTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
