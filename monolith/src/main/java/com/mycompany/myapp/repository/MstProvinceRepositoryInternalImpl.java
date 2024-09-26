package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstProvince;
import com.mycompany.myapp.domain.criteria.MstProvinceCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCountryRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstProvinceRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstProvince entity.
 */
@SuppressWarnings("unused")
class MstProvinceRepositoryInternalImpl extends SimpleR2dbcRepository<MstProvince, Long> implements MstProvinceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstCountryRowMapper mstcountryMapper;
    private final MstProvinceRowMapper mstprovinceMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_province", EntityManager.ENTITY_ALIAS);
    private static final Table countryTable = Table.aliased("mst_country", "country");

    public MstProvinceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstCountryRowMapper mstcountryMapper,
        MstProvinceRowMapper mstprovinceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstProvince.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstcountryMapper = mstcountryMapper;
        this.mstprovinceMapper = mstprovinceMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstProvince> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstProvince> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstProvinceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstCountrySqlHelper.getColumns(countryTable, "country"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(countryTable)
            .on(Column.create("country_id", entityTable))
            .equals(Column.create("id", countryTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstProvince.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstProvince> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstProvince> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<MstProvince> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<MstProvince> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<MstProvince> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private MstProvince process(Row row, RowMetadata metadata) {
        MstProvince entity = mstprovinceMapper.apply(row, "e");
        entity.setCountry(mstcountryMapper.apply(row, "country"));
        return entity;
    }

    @Override
    public <S extends MstProvince> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstProvince> findByCriteria(MstProvinceCriteria mstProvinceCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstProvinceCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstProvinceCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstProvinceCriteria criteria) {
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
            if (criteria.getCountryId() != null) {
                builder.buildFilterConditionForField(criteria.getCountryId(), countryTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
