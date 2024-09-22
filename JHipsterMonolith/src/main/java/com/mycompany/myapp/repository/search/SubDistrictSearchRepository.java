package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.SubDistrict;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link SubDistrict} entity.
 */
public interface SubDistrictSearchRepository
    extends ReactiveElasticsearchRepository<SubDistrict, Long>, SubDistrictSearchRepositoryInternal {}

interface SubDistrictSearchRepositoryInternal {
    Flux<SubDistrict> search(String query, Pageable pageable);

    Flux<SubDistrict> search(Query query);
}

class SubDistrictSearchRepositoryInternalImpl implements SubDistrictSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    SubDistrictSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<SubDistrict> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<SubDistrict> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, SubDistrict.class).map(SearchHit::getContent);
    }
}
