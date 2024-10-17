package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxOrder} entity.
 */
public interface TrxOrderSearchRepository extends ReactiveElasticsearchRepository<TrxOrder, Long>, TrxOrderSearchRepositoryInternal {}

interface TrxOrderSearchRepositoryInternal {
    Flux<TrxOrder> search(String query, Pageable pageable);

    Flux<TrxOrder> search(Query query);
}

class TrxOrderSearchRepositoryInternalImpl implements TrxOrderSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxOrderSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxOrder> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxOrder> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxOrder.class).map(SearchHit::getContent);
    }
}
