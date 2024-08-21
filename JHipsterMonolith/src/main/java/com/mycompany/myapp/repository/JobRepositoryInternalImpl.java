package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Job;
import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.domain.criteria.JobCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.EmployeeRowMapper;
import com.mycompany.myapp.repository.rowmapper.JobRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Job entity.
 */
@SuppressWarnings("unused")
class JobRepositoryInternalImpl extends SimpleR2dbcRepository<Job, Long> implements JobRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EmployeeRowMapper employeeMapper;
    private final JobRowMapper jobMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("job", EntityManager.ENTITY_ALIAS);
    private static final Table employeeTable = Table.aliased("employee", "employee");

    private static final EntityManager.LinkTable taskLink = new EntityManager.LinkTable("rel_job__task", "job_id", "task_id");

    public JobRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EmployeeRowMapper employeeMapper,
        JobRowMapper jobMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Job.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.employeeMapper = employeeMapper;
        this.jobMapper = jobMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Job> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Job> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = JobSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EmployeeSqlHelper.getColumns(employeeTable, "employee"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(employeeTable)
            .on(Column.create("employee_id", entityTable))
            .equals(Column.create("id", employeeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Job.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Job> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Job> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Job> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Job> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Job> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Job process(Row row, RowMetadata metadata) {
        Job entity = jobMapper.apply(row, "e");
        entity.setEmployee(employeeMapper.apply(row, "employee"));
        return entity;
    }

    @Override
    public <S extends Job> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Job> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(taskLink, entity.getId(), entity.getTasks().stream().map(Task::getId)).then();
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
    public Flux<Job> findByCriteria(JobCriteria jobCriteria, Pageable page) {
        return createQuery(page, buildConditions(jobCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(JobCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(JobCriteria criteria) {
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
