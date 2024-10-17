package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxOrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxOrderItem} entity.
 */
public interface TrxOrderItemSearchRepository
    extends ReactiveElasticsearchRepository<TrxOrderItem, Long>, TrxOrderItemSearchRepositoryInternal {}

interface TrxOrderItemSearchRepositoryInternal {
    Flux<TrxOrderItem> search(String query, Pageable pageable);

    Flux<TrxOrderItem> search(Query query);
}

class TrxOrderItemSearchRepositoryInternalImpl implements TrxOrderItemSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxOrderItemSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxOrderItem> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxOrderItem> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxOrderItem.class).map(SearchHit::getContent);
    }
}
