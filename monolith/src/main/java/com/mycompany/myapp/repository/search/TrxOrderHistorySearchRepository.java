package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxOrderHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxOrderHistory} entity.
 */
public interface TrxOrderHistorySearchRepository
    extends ReactiveElasticsearchRepository<TrxOrderHistory, Long>, TrxOrderHistorySearchRepositoryInternal {}

interface TrxOrderHistorySearchRepositoryInternal {
    Flux<TrxOrderHistory> search(String query, Pageable pageable);

    Flux<TrxOrderHistory> search(Query query);
}

class TrxOrderHistorySearchRepositoryInternalImpl implements TrxOrderHistorySearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxOrderHistorySearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxOrderHistory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxOrderHistory> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxOrderHistory.class).map(SearchHit::getContent);
    }
}
