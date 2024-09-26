package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstJob;
import com.mycompany.myapp.domain.MstTask;
import com.mycompany.myapp.domain.criteria.MstJobCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstEmployeeRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstJobRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstJob entity.
 */
@SuppressWarnings("unused")
class MstJobRepositoryInternalImpl extends SimpleR2dbcRepository<MstJob, Long> implements MstJobRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstEmployeeRowMapper mstemployeeMapper;
    private final MstJobRowMapper mstjobMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_job", EntityManager.ENTITY_ALIAS);
    private static final Table employeeTable = Table.aliased("mst_employee", "employee");

    private static final EntityManager.LinkTable taskLink = new EntityManager.LinkTable("rel_mst_job__task", "mst_job_id", "task_id");

    public MstJobRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstEmployeeRowMapper mstemployeeMapper,
        MstJobRowMapper mstjobMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstJob.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstemployeeMapper = mstemployeeMapper;
        this.mstjobMapper = mstjobMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstJob> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstJob> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstJobSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstEmployeeSqlHelper.getColumns(employeeTable, "employee"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(employeeTable)
            .on(Column.create("employee_id", entityTable))
            .equals(Column.create("id", employeeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstJob.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstJob> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstJob> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<MstJob> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<MstJob> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<MstJob> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private MstJob process(Row row, RowMetadata metadata) {
        MstJob entity = mstjobMapper.apply(row, "e");
        entity.setEmployee(mstemployeeMapper.apply(row, "employee"));
        return entity;
    }

    @Override
    public <S extends MstJob> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends MstJob> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(taskLink, entity.getId(), entity.getTasks().stream().map(MstTask::getId)).then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(taskLink, entityId);
    }

    @Override
    public Flux<MstJob> findByCriteria(MstJobCriteria mstJobCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstJobCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstJobCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstJobCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getJobTitle() != null) {
                builder.buildFilterConditionForField(criteria.getJobTitle(), entityTable.column("job_title"));
            }
            if (criteria.getMinSalary() != null) {
                builder.buildFilterConditionForField(criteria.getMinSalary(), entityTable.column("min_salary"));
            }
            if (criteria.getMaxSalary() != null) {
                builder.buildFilterConditionForField(criteria.getMaxSalary(), entityTable.column("max_salary"));
            }
            if (criteria.getEmployeeId() != null) {
                builder.buildFilterConditionForField(criteria.getEmployeeId(), employeeTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
