package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstCategory;
import com.mycompany.myapp.domain.criteria.MstCategoryCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCategoryRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstCategory entity.
 */
@SuppressWarnings("unused")
class MstCategoryRepositoryInternalImpl extends SimpleR2dbcRepository<MstCategory, Long> implements MstCategoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstCategoryRowMapper mstcategoryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_category", EntityManager.ENTITY_ALIAS);

    public MstCategoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstCategoryRowMapper mstcategoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstCategory.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstcategoryMapper = mstcategoryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstCategory> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstCategory> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstCategorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstCategory.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstCategory> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstCategory> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private MstCategory process(Row row, RowMetadata metadata) {
        MstCategory entity = mstcategoryMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends MstCategory> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstCategory> findByCriteria(MstCategoryCriteria mstCategoryCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstCategoryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstCategoryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstCategoryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getDescription() != null) {
                builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
            }
        }
        return builder.buildConditions();
    }
}
