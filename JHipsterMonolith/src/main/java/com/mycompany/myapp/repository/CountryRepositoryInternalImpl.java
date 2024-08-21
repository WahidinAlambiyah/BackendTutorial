package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Country;
import com.mycompany.myapp.domain.criteria.CountryCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.CountryRowMapper;
import com.mycompany.myapp.repository.rowmapper.RegionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Country entity.
 */
@SuppressWarnings("unused")
class CountryRepositoryInternalImpl extends SimpleR2dbcRepository<Country, Long> implements CountryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RegionRowMapper regionMapper;
    private final CountryRowMapper countryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("country", EntityManager.ENTITY_ALIAS);
    private static final Table regionTable = Table.aliased("region", "region");

    public CountryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RegionRowMapper regionMapper,
        CountryRowMapper countryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Country.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.regionMapper = regionMapper;
        this.countryMapper = countryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Country> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Country> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CountrySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RegionSqlHelper.getColumns(regionTable, "region"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(regionTable)
            .on(Column.create("region_id", entityTable))
            .equals(Column.create("id", regionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Country.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Country> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Country> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Country> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Country> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Country> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Country process(Row row, RowMetadata metadata) {
        Country entity = countryMapper.apply(row, "e");
        entity.setRegion(regionMapper.apply(row, "region"));
        return entity;
    }

    @Override
    public <S extends Country> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Country> findByCriteria(CountryCriteria countryCriteria, Pageable page) {
        return createQuery(page, buildConditions(countryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(CountryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(CountryCriteria criteria) {
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
            if (criteria.getRegionId() != null) {
                builder.buildFilterConditionForField(criteria.getRegionId(), regionTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
