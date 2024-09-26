package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TrxTournament;
import com.mycompany.myapp.domain.criteria.TrxTournamentCriteria;
import com.mycompany.myapp.repository.rowmapper.ColumnConverter;
import com.mycompany.myapp.repository.rowmapper.TrxEventRowMapper;
import com.mycompany.myapp.repository.rowmapper.TrxTournamentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TrxTournament entity.
 */
@SuppressWarnings("unused")
class TrxTournamentRepositoryInternalImpl extends SimpleR2dbcRepository<TrxTournament, Long> implements TrxTournamentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TrxEventRowMapper trxeventMapper;
    private final TrxTournamentRowMapper trxtournamentMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("trx_tournament", EntityManager.ENTITY_ALIAS);
    private static final Table eventTable = Table.aliased("trx_event", "event");

    public TrxTournamentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TrxEventRowMapper trxeventMapper,
        TrxTournamentRowMapper trxtournamentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TrxTournament.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.trxeventMapper = trxeventMapper;
        this.trxtournamentMapper = trxtournamentMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<TrxTournament> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TrxTournament> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrxTournamentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TrxEventSqlHelper.getColumns(eventTable, "event"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(eventTable)
            .on(Column.create("event_id", entityTable))
            .equals(Column.create("id", eventTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TrxTournament.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TrxTournament> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TrxTournament> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<TrxTournament> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<TrxTournament> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<TrxTournament> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private TrxTournament process(Row row, RowMetadata metadata) {
        TrxTournament entity = trxtournamentMapper.apply(row, "e");
        entity.setEvent(trxeventMapper.apply(row, "event"));
        return entity;
    }

    @Override
    public <S extends TrxTournament> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<TrxTournament> findByCriteria(TrxTournamentCriteria trxTournamentCriteria, Pageable page) {
        return createQuery(page, buildConditions(trxTournamentCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TrxTournamentCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TrxTournamentCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getType() != null) {
                builder.buildFilterConditionForField(criteria.getType(), entityTable.column("type"));
            }
            if (criteria.getPrizeAmount() != null) {
                builder.buildFilterConditionForField(criteria.getPrizeAmount(), entityTable.column("prize_amount"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getEndDate() != null) {
                builder.buildFilterConditionForField(criteria.getEndDate(), entityTable.column("end_date"));
            }
            if (criteria.getLocation() != null) {
                builder.buildFilterConditionForField(criteria.getLocation(), entityTable.column("location"));
            }
            if (criteria.getMaxParticipants() != null) {
                builder.buildFilterConditionForField(criteria.getMaxParticipants(), entityTable.column("max_participants"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getEventId() != null) {
                builder.buildFilterConditionForField(criteria.getEventId(), eventTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
