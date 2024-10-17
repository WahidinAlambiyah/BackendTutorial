package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstDriver;
import com.mycompany.myapp.domain.criteria.MstDriverCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstDriverRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstDriver entity.
 */
@SuppressWarnings("unused")
class MstDriverRepositoryInternalImpl extends SimpleR2dbcRepository<MstDriver, Long> implements MstDriverRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstDriverRowMapper mstdriverMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_driver", EntityManager.ENTITY_ALIAS);

    public MstDriverRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstDriverRowMapper mstdriverMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstDriver.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstdriverMapper = mstdriverMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstDriver> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstDriver> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstDriverSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstDriver.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstDriver> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstDriver> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private MstDriver process(Row row, RowMetadata metadata) {
        MstDriver entity = mstdriverMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends MstDriver> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstDriver> findByCriteria(MstDriverCriteria mstDriverCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstDriverCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstDriverCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstDriverCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getContactNumber() != null) {
                builder.buildFilterConditionForField(criteria.getContactNumber(), entityTable.column("contact_number"));
            }
            if (criteria.getVehicleDetails() != null) {
                builder.buildFilterConditionForField(criteria.getVehicleDetails(), entityTable.column("vehicle_details"));
            }
        }
        return builder.buildConditions();
    }
}
