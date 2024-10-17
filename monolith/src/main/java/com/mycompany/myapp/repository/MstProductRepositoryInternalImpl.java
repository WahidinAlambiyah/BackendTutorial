package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstProduct;
import com.mycompany.myapp.domain.criteria.MstProductCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstBrandRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstCategoryRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstProductRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstSupplierRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstProduct entity.
 */
@SuppressWarnings("unused")
class MstProductRepositoryInternalImpl extends SimpleR2dbcRepository<MstProduct, Long> implements MstProductRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstCategoryRowMapper mstcategoryMapper;
    private final MstBrandRowMapper mstbrandMapper;
    private final MstSupplierRowMapper mstsupplierMapper;
    private final MstProductRowMapper mstproductMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_product", EntityManager.ENTITY_ALIAS);
    private static final Table categoryTable = Table.aliased("mst_category", "category");
    private static final Table brandTable = Table.aliased("mst_brand", "brand");
    private static final Table mstSupplierTable = Table.aliased("mst_supplier", "mstSupplier");

    public MstProductRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstCategoryRowMapper mstcategoryMapper,
        MstBrandRowMapper mstbrandMapper,
        MstSupplierRowMapper mstsupplierMapper,
        MstProductRowMapper mstproductMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstProduct.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstcategoryMapper = mstcategoryMapper;
        this.mstbrandMapper = mstbrandMapper;
        this.mstsupplierMapper = mstsupplierMapper;
        this.mstproductMapper = mstproductMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstProduct> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstProduct> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstProductSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstCategorySqlHelper.getColumns(categoryTable, "category"));
        columns.addAll(MstBrandSqlHelper.getColumns(brandTable, "brand"));
        columns.addAll(MstSupplierSqlHelper.getColumns(mstSupplierTable, "mstSupplier"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categoryTable)
            .on(Column.create("category_id", entityTable))
            .equals(Column.create("id", categoryTable))
            .leftOuterJoin(brandTable)
            .on(Column.create("brand_id", entityTable))
            .equals(Column.create("id", brandTable))
            .leftOuterJoin(mstSupplierTable)
            .on(Column.create("mst_supplier_id", entityTable))
            .equals(Column.create("id", mstSupplierTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstProduct.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstProduct> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstProduct> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private MstProduct process(Row row, RowMetadata metadata) {
        MstProduct entity = mstproductMapper.apply(row, "e");
        entity.setCategory(mstcategoryMapper.apply(row, "category"));
        entity.setBrand(mstbrandMapper.apply(row, "brand"));
        entity.setMstSupplier(mstsupplierMapper.apply(row, "mstSupplier"));
        return entity;
    }

    @Override
    public <S extends MstProduct> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstProduct> findByCriteria(MstProductCriteria mstProductCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstProductCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstProductCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstProductCriteria criteria) {
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
            if (criteria.getPrice() != null) {
                builder.buildFilterConditionForField(criteria.getPrice(), entityTable.column("price"));
            }
            if (criteria.getQuantity() != null) {
                builder.buildFilterConditionForField(criteria.getQuantity(), entityTable.column("quantity"));
            }
            if (criteria.getBarcode() != null) {
                builder.buildFilterConditionForField(criteria.getBarcode(), entityTable.column("barcode"));
            }
            if (criteria.getUnitSize() != null) {
                builder.buildFilterConditionForField(criteria.getUnitSize(), entityTable.column("unit_size"));
            }
            if (criteria.getCategoryId() != null) {
                builder.buildFilterConditionForField(criteria.getCategoryId(), categoryTable.column("id"));
            }
            if (criteria.getBrandId() != null) {
                builder.buildFilterConditionForField(criteria.getBrandId(), brandTable.column("id"));
            }
            if (criteria.getMstSupplierId() != null) {
                builder.buildFilterConditionForField(criteria.getMstSupplierId(), mstSupplierTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
