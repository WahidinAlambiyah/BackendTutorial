package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SubDistrict;
import com.mycompany.myapp.domain.criteria.SubDistrictCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.DistrictRowMapper;
import com.mycompany.myapp.repository.rowmapper.SubDistrictRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the SubDistrict entity.
 */
@SuppressWarnings("unused")
class SubDistrictRepositoryInternalImpl extends SimpleR2dbcRepository<SubDistrict, Long> implements SubDistrictRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final DistrictRowMapper districtMapper;
    private final SubDistrictRowMapper subdistrictMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("sub_district", EntityManager.ENTITY_ALIAS);
    private static final Table districtTable = Table.aliased("district", "district");

    public SubDistrictRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        DistrictRowMapper districtMapper,
        SubDistrictRowMapper subdistrictMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(SubDistrict.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.districtMapper = districtMapper;
        this.subdistrictMapper = subdistrictMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<SubDistrict> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<SubDistrict> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SubDistrictSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(DistrictSqlHelper.getColumns(districtTable, "district"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(districtTable)
            .on(Column.create("district_id", entityTable))
            .equals(Column.create("id", districtTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, SubDistrict.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<SubDistrict> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<SubDistrict> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<SubDistrict> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<SubDistrict> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<SubDistrict> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private SubDistrict process(Row row, RowMetadata metadata) {
        SubDistrict entity = subdistrictMapper.apply(row, "e");
        entity.setDistrict(districtMapper.apply(row, "district"));
        return entity;
    }

    @Override
    public <S extends SubDistrict> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<SubDistrict> findByCriteria(SubDistrictCriteria subDistrictCriteria, Pageable page) {
        return createQuery(page, buildConditions(subDistrictCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(SubDistrictCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(SubDistrictCriteria criteria) {
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
