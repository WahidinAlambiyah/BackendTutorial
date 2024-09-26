package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxEvent;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxEvent} entity.
 */
public interface TrxEventSearchRepository extends ReactiveElasticsearchRepository<TrxEvent, Long>, TrxEventSearchRepositoryInternal {}

interface TrxEventSearchRepositoryInternal {
    Flux<TrxEvent> search(String query);

    Flux<TrxEvent> search(Query query);
}

class TrxEventSearchRepositoryInternalImpl implements TrxEventSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxEventSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxEvent> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxEvent> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxEvent.class).map(SearchHit::getContent);
    }
}
