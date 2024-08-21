package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.District;
import com.mycompany.myapp.domain.criteria.DistrictCriteria;
import com.mycompany.myapp.repository.rowmapper.CityRowMapper;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.DistrictRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the District entity.
 */
@SuppressWarnings("unused")
class DistrictRepositoryInternalImpl extends SimpleR2dbcRepository<District, Long> implements DistrictRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CityRowMapper cityMapper;
    private final DistrictRowMapper districtMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("district", EntityManager.ENTITY_ALIAS);
    private static final Table cityTable = Table.aliased("city", "city");

    public DistrictRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CityRowMapper cityMapper,
        DistrictRowMapper districtMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(District.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.cityMapper = cityMapper;
        this.districtMapper = districtMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<District> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<District> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DistrictSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CitySqlHelper.getColumns(cityTable, "city"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cityTable)
            .on(Column.create("city_id", entityTable))
            .equals(Column.create("id", cityTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, District.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<District> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<District> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<District> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<District> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<District> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private District process(Row row, RowMetadata metadata) {
        District entity = districtMapper.apply(row, "e");
        entity.setCity(cityMapper.apply(row, "city"));
        return entity;
    }

    @Override
    public <S extends District> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<District> findByCriteria(DistrictCriteria districtCriteria, Pageable page) {
        return createQuery(page, buildConditions(districtCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(DistrictCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(DistrictCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getUnm49Code() != null) {
                builder.buildFilterConditionForField(criteria.getUnm49Code(), entityTable.column("unm_49_code"));
            }
            if (criteria.getIsoAlpha2Code() != null) {
                builder.buildFilterConditionForField(criteria.getIsoAlpha2Code(), entityTable.column("iso_alpha_2_code"));
            }
            if (criteria.getCityId() != null) {
                builder.buildFilterConditionForField(criteria.getCityId(), cityTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
