package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PostalCode;
import com.mycompany.myapp.repository.rowmapper.PostalCodeRowMapper;
import com.mycompany.myapp.repository.rowmapper.SubDistrictRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the PostalCode entity.
 */
@SuppressWarnings("unused")
class PostalCodeRepositoryInternalImpl extends SimpleR2dbcRepository<PostalCode, Long> implements PostalCodeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SubDistrictRowMapper subdistrictMapper;
    private final PostalCodeRowMapper postalcodeMapper;

    private static final Table entityTable = Table.aliased("postal_code", EntityManager.ENTITY_ALIAS);
    private static final Table subDistrictTable = Table.aliased("sub_district", "subDistrict");

    public PostalCodeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SubDistrictRowMapper subdistrictMapper,
        PostalCodeRowMapper postalcodeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(PostalCode.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.subdistrictMapper = subdistrictMapper;
        this.postalcodeMapper = postalcodeMapper;
    }

    @Override
    public Flux<PostalCode> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<PostalCode> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PostalCodeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(SubDistrictSqlHelper.getColumns(subDistrictTable, "subDistrict"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(subDistrictTable)
            .on(Column.create("sub_district_id", entityTable))
            .equals(Column.create("id", subDistrictTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, PostalCode.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<PostalCode> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<PostalCode> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<PostalCode> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<PostalCode> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<PostalCode> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private PostalCode process(Row row, RowMetadata metadata) {
        PostalCode entity = postalcodeMapper.apply(row, "e");
        entity.setSubDistrict(subdistrictMapper.apply(row, "subDistrict"));
        return entity;
    }

    @Override
    public <S extends PostalCode> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
