package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstCity;
import com.mycompany.myapp.domain.criteria.MstCityCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCityRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstCity entity.
 */
@SuppressWarnings("unused")
class MstCityRepositoryInternalImpl extends SimpleR2dbcRepository<MstCity, Long> implements MstCityRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstProvinceRowMapper mstprovinceMapper;
    private final MstCityRowMapper mstcityMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_city", EntityManager.ENTITY_ALIAS);
    private static final Table provinceTable = Table.aliased("mst_province", "province");

    public MstCityRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstProvinceRowMapper mstprovinceMapper,
        MstCityRowMapper mstcityMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstCity.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstprovinceMapper = mstprovinceMapper;
        this.mstcityMapper = mstcityMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstCity> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstCity> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstCitySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstProvinceSqlHelper.getColumns(provinceTable, "province"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(provinceTable)
            .on(Column.create("province_id", entityTable))
            .equals(Column.create("id", provinceTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstCity.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstCity> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstCity> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<MstCity> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<MstCity> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<MstCity> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private MstCity process(Row row, RowMetadata metadata) {
        MstCity entity = mstcityMapper.apply(row, "e");
        entity.setProvince(mstprovinceMapper.apply(row, "province"));
        return entity;
    }

    @Override
    public <S extends MstCity> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstCity> findByCriteria(MstCityCriteria mstCityCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstCityCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstCityCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstCityCriteria criteria) {
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
            if (criteria.getProvinceId() != null) {
                builder.buildFilterConditionForField(criteria.getProvinceId(), provinceTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
