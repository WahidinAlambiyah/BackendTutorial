package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstDistrict;
import com.mycompany.myapp.domain.criteria.MstDistrictCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCityRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstDistrictRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstDistrict entity.
 */
@SuppressWarnings("unused")
class MstDistrictRepositoryInternalImpl extends SimpleR2dbcRepository<MstDistrict, Long> implements MstDistrictRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstCityRowMapper mstcityMapper;
    private final MstDistrictRowMapper mstdistrictMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_district", EntityManager.ENTITY_ALIAS);
    private static final Table cityTable = Table.aliased("mst_city", "city");

    public MstDistrictRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstCityRowMapper mstcityMapper,
        MstDistrictRowMapper mstdistrictMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstDistrict.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstcityMapper = mstcityMapper;
        this.mstdistrictMapper = mstdistrictMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstDistrict> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstDistrict> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstDistrictSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstCitySqlHelper.getColumns(cityTable, "city"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cityTable)
            .on(Column.create("city_id", entityTable))
            .equals(Column.create("id", cityTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstDistrict.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstDistrict> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstDistrict> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<MstDistrict> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<MstDistrict> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<MstDistrict> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private MstDistrict process(Row row, RowMetadata metadata) {
        MstDistrict entity = mstdistrictMapper.apply(row, "e");
        entity.setCity(mstcityMapper.apply(row, "city"));
        return entity;
    }

    @Override
    public <S extends MstDistrict> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstDistrict> findByCriteria(MstDistrictCriteria mstDistrictCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstDistrictCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstDistrictCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstDistrictCriteria criteria) {
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
