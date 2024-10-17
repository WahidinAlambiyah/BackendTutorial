package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstBrand;
import com.mycompany.myapp.domain.criteria.MstBrandCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstBrandRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the MstBrand entity.
 */
@SuppressWarnings("unused")
class MstBrandRepositoryInternalImpl extends SimpleR2dbcRepository<MstBrand, Long> implements MstBrandRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstBrandRowMapper mstbrandMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_brand", EntityManager.ENTITY_ALIAS);

    public MstBrandRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstBrandRowMapper mstbrandMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstBrand.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstbrandMapper = mstbrandMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstBrand> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstBrand> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstBrandSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstBrand.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstBrand> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstBrand> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private MstBrand process(Row row, RowMetadata metadata) {
        MstBrand entity = mstbrandMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends MstBrand> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstBrand> findByCriteria(MstBrandCriteria mstBrandCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstBrandCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstBrandCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstBrandCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getLogo() != null) {
                builder.buildFilterConditionForField(criteria.getLogo(), entityTable.column("logo"));
            }
            if (criteria.getDescription() != null) {
                builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
            }
        }
        return builder.buildConditions();
    }
}
