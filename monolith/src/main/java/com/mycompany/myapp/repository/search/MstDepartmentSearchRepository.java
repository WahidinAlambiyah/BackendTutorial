package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.MstDepartment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MstDepartment} entity.
 */
public interface MstDepartmentSearchRepository
    extends ReactiveElasticsearchRepository<MstDepartment, Long>, MstDepartmentSearchRepositoryInternal {}

interface MstDepartmentSearchRepositoryInternal {
    Flux<MstDepartment> search(String query, Pageable pageable);

    Flux<MstDepartment> search(Query query);
}

class MstDepartmentSearchRepositoryInternalImpl implements MstDepartmentSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MstDepartmentSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MstDepartment> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MstDepartment> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MstDepartment.class).map(SearchHit::getContent);
    }
}
