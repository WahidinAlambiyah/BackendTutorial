package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxNotification;
import com.mycompany.myapp.domain.criteria.TrxNotificationCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.MstCustomerRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrxNotificationRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxNotification entity.
 */
@SuppressWarnings("unused")
class TrxNotificationRepositoryInternalImpl
    extends SimpleR2dbcRepository<TrxNotification, Long>
    implements TrxNotificationRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MstCustomerRowMapper mstcustomerMapper;
    private final TrxNotificationRowMapper trxnotificationMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_notification", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("mst_customer", "customer");

    public TrxNotificationRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MstCustomerRowMapper mstcustomerMapper,
        TrxNotificationRowMapper trxnotificationMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxNotification.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.mstcustomerMapper = mstcustomerMapper;
        this.trxnotificationMapper = trxnotificationMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxNotification> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxNotification> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxNotificationSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MstCustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxNotification.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxNotification> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxNotification> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TrxNotification process(Row row, RowMetadata metadata) {
        TrxNotification entity = trxnotificationMapper.apply(row, "e");
        entity.setCustomer(mstcustomerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends TrxNotification> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxNotification> findByCriteria(TrxNotificationCriteria trxNotificationCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxNotificationCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxNotificationCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxNotificationCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getRecipient() != null) {
                builder.buildFilterConditionForField(criteria.getRecipient(), entityTable.column("recipient"));
            }
            if (criteria.getMessageType() != null) {
                builder.buildFilterConditionForField(criteria.getMessageType(), entityTable.column("message_type"));
            }
            if (criteria.getContent() != null) {
                builder.buildFilterConditionForField(criteria.getContent(), entityTable.column("content"));
            }
            if (criteria.getSentAt() != null) {
                builder.buildFilterConditionForField(criteria.getSentAt(), entityTable.column("sent_at"));
            }
            if (criteria.getCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getCustomerId(), customerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
