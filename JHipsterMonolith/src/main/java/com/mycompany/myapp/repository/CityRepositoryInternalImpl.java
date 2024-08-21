package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.City;
import com.mycompany.myapp.repository.rowmapper.CityRowMapper;
import com.mycompany.myapp.repository.rowmapper.ProvinceRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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

/**
 * Spring Data R2DBC custom repository implementation for the City entity.
 */
@SuppressWarnings("unused")
class CityRepositoryInternalImpl extends SimpleR2dbcRepository<City, Long> implements CityRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProvinceRowMapper provinceMapper;
    private final CityRowMapper cityMapper;

    private static final Table entityTable = Table.aliased("city", EntityManager.ENTITY_ALIAS);
    private static final Table provinceTable = Table.aliased("province", "province");

    public CityRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProvinceRowMapper provinceMapper,
        CityRowMapper cityMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(City.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.provinceMapper = provinceMapper;
        this.cityMapper = cityMapper;
    }

    @Override
    public Flux<City> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<City> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CitySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProvinceSqlHelper.getColumns(provinceTable, "province"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(provinceTable)
            .on(Column.create("province_id", entityTable))
            .equals(Column.create("id", provinceTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, City.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<City> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<City> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<City> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<City> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<City> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private City process(Row row, RowMetadata metadata) {
        City entity = cityMapper.apply(row, "e");
        entity.setProvince(provinceMapper.apply(row, "province"));
        return entity;
    }

    @Override
    public <S extends City> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
