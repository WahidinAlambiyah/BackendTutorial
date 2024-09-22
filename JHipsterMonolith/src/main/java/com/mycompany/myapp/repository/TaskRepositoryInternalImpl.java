package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.domain.criteria.TaskCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.TaskRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Task entity.
 */
@SuppressWarnings("unused")
class TaskRepositoryInternalImpl extends SimpleR2dbcRepository<Task, Long> implements TaskRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TaskRowMapper taskMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("task", EntityManager.ENTITY_ALIAS);

    public TaskRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TaskRowMapper taskMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Task.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.taskMapper = taskMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Task> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Task> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TaskSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Task.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Task> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Task> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Task process(Row row, RowMetadata metadata) {
        Task entity = taskMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Task> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Task> findByCriteria(TaskCriteria taskCriteria, Pageable page) {
        return createQuery(page, buildConditions(taskCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TaskCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TaskCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getTitle() != null) {
                builder.buildFilterConditionForField(criteria.getTitle(), entityTable.column("title"));
            }
            if (criteria.getDescription() != null) {
                builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
            }
        }
        return builder.buildConditions();
    }
}
