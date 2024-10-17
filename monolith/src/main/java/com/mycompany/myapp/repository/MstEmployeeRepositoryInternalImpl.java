package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstEmployee;
import com.mycompany.myapp.domain.criteria.MstEmployeeCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstDepartmentRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstEmployeeRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstEmployeeRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstEmployee entity.
 */
@SuppressWarnings("unused")
class MstEmployeeRepositoryInternalImpl extends SimpleR2dbcRepository<MstEmployee, Long> implements MstEmployeeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstEmployeeRowMapper mstemployeeMapper;
    private final MstDepartmentRowMapper mstdepartmentMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_employee", EntityManager.ENTITY_ALIAS);
    private static final Table managerTable = Table.aliased("mst_employee", "manager");
    private static final Table departmentTable = Table.aliased("mst_department", "department");
    private static final Table mstDepartmentTable = Table.aliased("mst_department", "mstDepartment");

    public MstEmployeeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstEmployeeRowMapper mstemployeeMapper,
        MstDepartmentRowMapper mstdepartmentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstEmployee.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstemployeeMapper = mstemployeeMapper;
        this.mstdepartmentMapper = mstdepartmentMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstEmployee> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstEmployee> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstEmployeeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstEmployeeSqlHelper.getColumns(managerTable, "manager"));
        columns.addAll(MstDepartmentSqlHelper.getColumns(departmentTable, "department"));
        columns.addAll(MstDepartmentSqlHelper.getColumns(mstDepartmentTable, "mstDepartment"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(managerTable)
            .on(Column.create("manager_id", entityTable))
            .equals(Column.create("id", managerTable))
            .leftOuterJoin(departmentTable)
            .on(Column.create("department_id", entityTable))
            .equals(Column.create("id", departmentTable))
            .leftOuterJoin(mstDepartmentTable)
            .on(Column.create("mst_department_id", entityTable))
            .equals(Column.create("id", mstDepartmentTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstEmployee.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstEmployee> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstEmployee> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private MstEmployee process(Row row, RowMetadata metadata) {
        MstEmployee entity = mstemployeeMapper.apply(row, "e");
        entity.setManager(mstemployeeMapper.apply(row, "manager"));
        entity.setDepartment(mstdepartmentMapper.apply(row, "department"));
        entity.setMstDepartment(mstdepartmentMapper.apply(row, "mstDepartment"));
        return entity;
    }

    @Override
    public <S extends MstEmployee> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstEmployee> findByCriteria(MstEmployeeCriteria mstEmployeeCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstEmployeeCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstEmployeeCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstEmployeeCriteria criteria) {
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
            if (criteria.getMstDepartmentId() != null) {
                builder.buildFilterConditionForField(criteria.getMstDepartmentId(), mstDepartmentTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
