package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tournament;
import com.mycompany.myapp.repository.rowmapper.EventRowMapper;
import com.mycompany.myapp.repository.rowmapper.TournamentRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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

/**
 * Spring Data R2DBC custom repository implementation for the Tournament entity.
 */
@SuppressWarnings("unused")
class TournamentRepositoryInternalImpl extends SimpleR2dbcRepository<Tournament, Long> implements TournamentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EventRowMapper eventMapper;
    private final TournamentRowMapper tournamentMapper;

    private static final Table entityTable = Table.aliased("tournament", EntityManager.ENTITY_ALIAS);
    private static final Table eventTable = Table.aliased("event", "event");

    public TournamentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EventRowMapper eventMapper,
        TournamentRowMapper tournamentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Tournament.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.eventMapper = eventMapper;
        this.tournamentMapper = tournamentMapper;
    }

    @Override
    public Flux<Tournament> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Tournament> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TournamentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EventSqlHelper.getColumns(eventTable, "event"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(eventTable)
            .on(Column.create("event_id", entityTable))
            .equals(Column.create("id", eventTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Tournament.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Tournament> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Tournament> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Tournament> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Tournament> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Tournament> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Tournament process(Row row, RowMetadata metadata) {
        Tournament entity = tournamentMapper.apply(row, "e");
        entity.setEvent(eventMapper.apply(row, "event"));
        return entity;
    }

    @Override
    public <S extends Tournament> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
