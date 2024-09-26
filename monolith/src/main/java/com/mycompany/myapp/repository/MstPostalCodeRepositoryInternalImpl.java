package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstPostalCode;
import com.mycompany.myapp.domain.criteria.MstPostalCodeCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstPostalCodeRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstPostalCode entity.
 */
@SuppressWarnings("unused")
class MstPostalCodeRepositoryInternalImpl extends SimpleR2dbcRepository<MstPostalCode, Long> implements MstPostalCodeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstSubDistrictRowMapper mstsubdistrictMapper;
    private final MstPostalCodeRowMapper mstpostalcodeMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_postal_code", EntityManager.ENTITY_ALIAS);
    private static final Table subDistrictTable = Table.aliased("mst_sub_district", "subDistrict");

    public MstPostalCodeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstSubDistrictRowMapper mstsubdistrictMapper,
        MstPostalCodeRowMapper mstpostalcodeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstPostalCode.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstsubdistrictMapper = mstsubdistrictMapper;
        this.mstpostalcodeMapper = mstpostalcodeMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstPostalCode> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstPostalCode> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstPostalCodeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstSubDistrictSqlHelper.getColumns(subDistrictTable, "subDistrict"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(subDistrictTable)
            .on(Column.create("sub_district_id", entityTable))
            .equals(Column.create("id", subDistrictTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstPostalCode.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstPostalCode> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstPostalCode> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<MstPostalCode> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<MstPostalCode> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<MstPostalCode> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private MstPostalCode process(Row row, RowMetadata metadata) {
        MstPostalCode entity = mstpostalcodeMapper.apply(row, "e");
        entity.setSubDistrict(mstsubdistrictMapper.apply(row, "subDistrict"));
        return entity;
    }

    @Override
    public <S extends MstPostalCode> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstPostalCode> findByCriteria(MstPostalCodeCriteria mstPostalCodeCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstPostalCodeCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstPostalCodeCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstPostalCodeCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getCode() != null) {
                builder.buildFilterConditionForField(criteria.getCode(), entityTable.column("code"));
            }
            if (criteria.getSubDistrictId() != null) {
                builder.buildFilterConditionForField(criteria.getSubDistrictId(), subDistrictTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
