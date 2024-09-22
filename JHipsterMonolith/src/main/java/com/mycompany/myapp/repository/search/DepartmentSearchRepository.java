package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.Department;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Department} entity.
 */
public interface DepartmentSearchRepository extends ReactiveElasticsearchRepository<Department, Long>, DepartmentSearchRepositoryInternal {}

interface DepartmentSearchRepositoryInternal {
    Flux<Department> search(String query);

    Flux<Department> search(Query query);
}

class DepartmentSearchRepositoryInternalImpl implements DepartmentSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    DepartmentSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Department> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Flux<Department> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Department.class).map(SearchHit::getContent);
    }
}
