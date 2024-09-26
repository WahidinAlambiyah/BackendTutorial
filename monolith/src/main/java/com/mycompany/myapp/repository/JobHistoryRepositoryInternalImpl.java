package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.JobHistory;
import com.mycompany.myapp.domain.criteria.JobHistoryCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.JobHistoryRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstDepartmentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the JobHistory entity.
 */
@SuppressWarnings("unused")
class JobHistoryRepositoryInternalImpl extends SimpleR2dbcRepository<JobHistory, Long> implements JobHistoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstJobRowMapper mstjobMapper;
    private final MstDepartmentRowMapper mstdepartmentMapper;
    private final MstEmployeeRowMapper mstemployeeMapper;
    private final JobHistoryRowMapper jobhistoryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("job_history", EntityManager.ENTITY_ALIAS);
    private static final Table jobTable = Table.aliased("mst_job", "job");
    private static final Table departmentTable = Table.aliased("mst_department", "department");
    private static final Table employeeTable = Table.aliased("mst_employee", "employee");

    public JobHistoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstJobRowMapper mstjobMapper,
        MstDepartmentRowMapper mstdepartmentMapper,
        MstEmployeeRowMapper mstemployeeMapper,
        JobHistoryRowMapper jobhistoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(JobHistory.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstjobMapper = mstjobMapper;
        this.mstdepartmentMapper = mstdepartmentMapper;
        this.mstemployeeMapper = mstemployeeMapper;
        this.jobhistoryMapper = jobhistoryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<JobHistory> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<JobHistory> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = JobHistorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstJobSqlHelper.getColumns(jobTable, "job"));
        columns.addAll(MstDepartmentSqlHelper.getColumns(departmentTable, "department"));
        columns.addAll(MstEmployeeSqlHelper.getColumns(employeeTable, "employee"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(jobTable)
            .on(Column.create("job_id", entityTable))
            .equals(Column.create("id", jobTable))
            .leftOuterJoin(departmentTable)
            .on(Column.create("department_id", entityTable))
            .equals(Column.create("id", departmentTable))
            .leftOuterJoin(employeeTable)
            .on(Column.create("employee_id", entityTable))
            .equals(Column.create("id", employeeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, JobHistory.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<JobHistory> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<JobHistory> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private JobHistory process(Row row, RowMetadata metadata) {
        JobHistory entity = jobhistoryMapper.apply(row, "e");
        entity.setJob(mstjobMapper.apply(row, "job"));
        entity.setDepartment(mstdepartmentMapper.apply(row, "department"));
        entity.setEmployee(mstemployeeMapper.apply(row, "employee"));
        return entity;
    }

    @Override
    public <S extends JobHistory> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<JobHistory> findByCriteria(JobHistoryCriteria jobHistoryCriteria, Pageable page) {
        return createQuery(page, buildConditions(jobHistoryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(JobHistoryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(JobHistoryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getEndDate() != null) {
                builder.buildFilterConditionForField(criteria.getEndDate(), entityTable.column("end_date"));
            }
            if (criteria.getLanguage() != null) {
                builder.buildFilterConditionForField(criteria.getLanguage(), entityTable.column("language"));
            }
            if (criteria.getJobId() != null) {
                builder.buildFilterConditionForField(criteria.getJobId(), jobTable.column("id"));
            }
            if (criteria.getDepartmentId() != null) {
                builder.buildFilterConditionForField(criteria.getDepartmentId(), departmentTable.column("id"));
            }
            if (criteria.getEmployeeId() != null) {
                builder.buildFilterConditionForField(criteria.getEmployeeId(), employeeTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
