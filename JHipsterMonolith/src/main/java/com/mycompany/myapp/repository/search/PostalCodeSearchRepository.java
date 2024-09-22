package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.PostalCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link PostalCode} entity.
 */
public interface PostalCodeSearchRepository extends ReactiveElasticsearchRepository<PostalCode, Long>, PostalCodeSearchRepositoryInternal {}

interface PostalCodeSearchRepositoryInternal {
    Flux<PostalCode> search(String query, Pageable pageable);

    Flux<PostalCode> search(Query query);
}

class PostalCodeSearchRepositoryInternalImpl implements PostalCodeSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PostalCodeSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<PostalCode> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<PostalCode> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, PostalCode.class).map(SearchHit::getContent);
    }
}
