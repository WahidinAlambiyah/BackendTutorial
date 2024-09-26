package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.MstPostalCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MstPostalCode} entity.
 */
public interface MstPostalCodeSearchRepository
    extends ReactiveElasticsearchRepository<MstPostalCode, Long>, MstPostalCodeSearchRepositoryInternal {}

interface MstPostalCodeSearchRepositoryInternal {
    Flux<MstPostalCode> search(String query, Pageable pageable);

    Flux<MstPostalCode> search(Query query);
}

class MstPostalCodeSearchRepositoryInternalImpl implements MstPostalCodeSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MstPostalCodeSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MstPostalCode> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MstPostalCode> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MstPostalCode.class).map(SearchHit::getContent);
    }
}
