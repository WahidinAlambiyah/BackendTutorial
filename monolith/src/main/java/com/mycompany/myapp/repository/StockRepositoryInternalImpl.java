package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Stock;
import com.mycompany.myapp.domain.criteria.StockCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstProductRowMapper;
import com.mycompany.myapp.repository.rowmapper.StockRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Stock entity.
 */
@SuppressWarnings("unused")
class StockRepositoryInternalImpl extends SimpleR2dbcRepository<Stock, Long> implements StockRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstProductRowMapper mstproductMapper;
    private final StockRowMapper stockMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("stock", EntityManager.ENTITY_ALIAS);
    private static final Table productTable = Table.aliased("mst_product", "product");

    public StockRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstProductRowMapper mstproductMapper,
        StockRowMapper stockMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Stock.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstproductMapper = mstproductMapper;
        this.stockMapper = stockMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Stock> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Stock> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = StockSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Stock.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Stock> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Stock> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Stock process(Row row, RowMetadata metadata) {
        Stock entity = stockMapper.apply(row, "e");
        entity.setProduct(mstproductMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends Stock> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Stock> findByCriteria(StockCriteria stockCriteria, Pageable page) {
        return createQuery(page, buildConditions(stockCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(StockCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(StockCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getQuantityAvailable() != null) {
                builder.buildFilterConditionForField(criteria.getQuantityAvailable(), entityTable.column("quantity_available"));
            }
            if (criteria.getReorderLevel() != null) {
                builder.buildFilterConditionForField(criteria.getReorderLevel(), entityTable.column("reorder_level"));
            }
            if (criteria.getExpiryDate() != null) {
                builder.buildFilterConditionForField(criteria.getExpiryDate(), entityTable.column("expiry_date"));
            }
            if (criteria.getProductId() != null) {
                builder.buildFilterConditionForField(criteria.getProductId(), productTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
