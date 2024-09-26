package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstSubDistrict;
import com.mycompany.myapp.domain.criteria.MstSubDistrictCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstDistrictRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstSubDistrictRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstSubDistrict entity.
 */
@SuppressWarnings("unused")
class MstSubDistrictRepositoryInternalImpl extends SimpleR2dbcRepository<MstSubDistrict, Long> implements MstSubDistrictRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstDistrictRowMapper mstdistrictMapper;
    private final MstSubDistrictRowMapper mstsubdistrictMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_sub_district", EntityManager.ENTITY_ALIAS);
    private static final Table districtTable = Table.aliased("mst_district", "district");

    public MstSubDistrictRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstDistrictRowMapper mstdistrictMapper,
        MstSubDistrictRowMapper mstsubdistrictMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstSubDistrict.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstdistrictMapper = mstdistrictMapper;
        this.mstsubdistrictMapper = mstsubdistrictMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstSubDistrict> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstSubDistrict> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstSubDistrictSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstDistrictSqlHelper.getColumns(districtTable, "district"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(districtTable)
            .on(Column.create("district_id", entityTable))
            .equals(Column.create("id", districtTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstSubDistrict.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstSubDistrict> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstSubDistrict> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<MstSubDistrict> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<MstSubDistrict> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<MstSubDistrict> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private MstSubDistrict process(Row row, RowMetadata metadata) {
        MstSubDistrict entity = mstsubdistrictMapper.apply(row, "e");
        entity.setDistrict(mstdistrictMapper.apply(row, "district"));
        return entity;
    }

    @Override
    public <S extends MstSubDistrict> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstSubDistrict> findByCriteria(MstSubDistrictCriteria mstSubDistrictCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstSubDistrictCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstSubDistrictCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstSubDistrictCriteria criteria) {
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
            if (criteria.getDistrictId() != null) {
                builder.buildFilterConditionForField(criteria.getDistrictId(), districtTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
