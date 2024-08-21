package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.criteria.EmployeeCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.DepartmentRowMapper;
import com.mycompany.myapp.repository.rowmapper.EmployeeRowMapper;
import com.mycompany.myapp.repository.rowmapper.EmployeeRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Employee entity.
 */
@SuppressWarnings("unused")
class EmployeeRepositoryInternalImpl extends SimpleR2dbcRepository<Employee, Long> implements EmployeeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EmployeeRowMapper employeeMapper;
    private final DepartmentRowMapper departmentMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("employee", EntityManager.ENTITY_ALIAS);
    private static final Table managerTable = Table.aliased("employee", "manager");
    private static final Table departmentTable = Table.aliased("department", "department");

    public EmployeeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EmployeeRowMapper employeeMapper,
        DepartmentRowMapper departmentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Employee.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Employee> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Employee> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EmployeeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EmployeeSqlHelper.getColumns(managerTable, "manager"));
        columns.addAll(DepartmentSqlHelper.getColumns(departmentTable, "department"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(managerTable)
            .on(Column.create("manager_id", entityTable))
            .equals(Column.create("id", managerTable))
            .leftOuterJoin(departmentTable)
            .on(Column.create("department_id", entityTable))
            .equals(Column.create("id", departmentTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Employee.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Employee> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Employee> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Employee process(Row row, RowMetadata metadata) {
        Employee entity = employeeMapper.apply(row, "e");
        entity.setManager(employeeMapper.apply(row, "manager"));
        entity.setDepartment(departmentMapper.apply(row, "department"));
        return entity;
    }

    @Override
    public <S extends Employee> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Employee> findByCriteria(EmployeeCriteria employeeCriteria, Pageable page) {
        return createQuery(page, buildConditions(employeeCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(EmployeeCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(EmployeeCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getFirstName() != null) {
                builder.buildFilterConditionForField(criteria.getFirstName(), entityTable.column("first_name"));
            }
            if (criteria.getLastName() != null) {
                builder.buildFilterConditionForField(criteria.getLastName(), entityTable.column("last_name"));
            }
            if (criteria.getEmail() != null) {
                builder.buildFilterConditionForField(criteria.getEmail(), entityTable.column("email"));
            }
            if (criteria.getPhoneNumber() != null) {
                builder.buildFilterConditionForField(criteria.getPhoneNumber(), entityTable.column("phone_number"));
            }
            if (criteria.getHireDate() != null) {
                builder.buildFilterConditionForField(criteria.getHireDate(), entityTable.column("hire_date"));
            }
            if (criteria.getSalary() != null) {
                builder.buildFilterConditionForField(criteria.getSalary(), entityTable.column("salary"));
            }
            if (criteria.getCommissionPct() != null) {
                builder.buildFilterConditionForField(criteria.getCommissionPct(), entityTable.column("commission_pct"));
            }
            if (criteria.getManagerId() != null) {
                builder.buildFilterConditionForField(criteria.getManagerId(), managerTable.column("id"));
            }
            if (criteria.getDepartmentId() != null) {
                builder.buildFilterConditionForField(criteria.getDepartmentId(), departmentTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
