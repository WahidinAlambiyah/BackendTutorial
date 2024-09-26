package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstCountry;
import com.mycompany.myapp.domain.criteria.MstCountryCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCountryRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstRegionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstCountry entity.
 */
@SuppressWarnings("unused")
class MstCountryRepositoryInternalImpl extends SimpleR2dbcRepository<MstCountry, Long> implements MstCountryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstRegionRowMapper mstregionMapper;
    private final MstCountryRowMapper mstcountryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_country", EntityManager.ENTITY_ALIAS);
    private static final Table regionTable = Table.aliased("mst_region", "region");

    public MstCountryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstRegionRowMapper mstregionMapper,
        MstCountryRowMapper mstcountryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstCountry.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstregionMapper = mstregionMapper;
        this.mstcountryMapper = mstcountryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstCountry> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstCountry> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstCountrySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstRegionSqlHelper.getColumns(regionTable, "region"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(regionTable)
            .on(Column.create("region_id", entityTable))
            .equals(Column.create("id", regionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstCountry.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstCountry> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstCountry> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<MstCountry> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<MstCountry> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<MstCountry> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private MstCountry process(Row row, RowMetadata metadata) {
        MstCountry entity = mstcountryMapper.apply(row, "e");
        entity.setRegion(mstregionMapper.apply(row, "region"));
        return entity;
    }

    @Override
    public <S extends MstCountry> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstCountry> findByCriteria(MstCountryCriteria mstCountryCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstCountryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstCountryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstCountryCriteria criteria) {
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
