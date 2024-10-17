package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MstLoyaltyProgram;
import com.mycompany.myapp.domain.criteria.MstLoyaltyProgramCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCustomerRowMapper;
import com.mycompany.myapp.repository.rowmapper.MstLoyaltyProgramRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the MstLoyaltyProgram entity.
 */
@SuppressWarnings("unused")
class MstLoyaltyProgramRepositoryInternalImpl
    extends SimpleR2dbcRepository<MstLoyaltyProgram, Long>
    implements MstLoyaltyProgramRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstCustomerRowMapper mstcustomerMapper;
    private final MstLoyaltyProgramRowMapper mstloyaltyprogramMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("mst_loyalty_program", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("mst_customer", "customer");

    public MstLoyaltyProgramRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstCustomerRowMapper mstcustomerMapper,
        MstLoyaltyProgramRowMapper mstloyaltyprogramMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(MstLoyaltyProgram.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstcustomerMapper = mstcustomerMapper;
        this.mstloyaltyprogramMapper = mstloyaltyprogramMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<MstLoyaltyProgram> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<MstLoyaltyProgram> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MstLoyaltyProgramSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstCustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, MstLoyaltyProgram.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<MstLoyaltyProgram> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<MstLoyaltyProgram> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private MstLoyaltyProgram process(Row row, RowMetadata metadata) {
        MstLoyaltyProgram entity = mstloyaltyprogramMapper.apply(row, "e");
        entity.setCustomer(mstcustomerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends MstLoyaltyProgram> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<MstLoyaltyProgram> findByCriteria(MstLoyaltyProgramCriteria mstLoyaltyProgramCriteria, Pageable page) {
        return createQuery(page, buildConditions(mstLoyaltyProgramCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MstLoyaltyProgramCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(MstLoyaltyProgramCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getPointsEarned() != null) {
                builder.buildFilterConditionForField(criteria.getPointsEarned(), entityTable.column("points_earned"));
            }
            if (criteria.getMembershipTier() != null) {
                builder.buildFilterConditionForField(criteria.getMembershipTier(), entityTable.column("membership_tier"));
            }
            if (criteria.getCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getCustomerId(), customerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
