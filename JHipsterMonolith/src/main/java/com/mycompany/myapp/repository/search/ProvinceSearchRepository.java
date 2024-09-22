package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.Province;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Province} entity.
 */
public interface ProvinceSearchRepository extends ReactiveElasticsearchRepository<Province, Long>, ProvinceSearchRepositoryInternal {}

interface ProvinceSearchRepositoryInternal {
    Flux<Province> search(String query, Pageable pageable);

    Flux<Province> search(Query query);
}

class ProvinceSearchRepositoryInternalImpl implements ProvinceSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ProvinceSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Province> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Province> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Province.class).map(SearchHit::getContent);
    }
}
