package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxOrderStock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxOrderStock} entity.
 */
public interface TrxOrderStockSearchRepository
    extends ReactiveElasticsearchRepository<TrxOrderStock, Long>, TrxOrderStockSearchRepositoryInternal {}

interface TrxOrderStockSearchRepositoryInternal {
    Flux<TrxOrderStock> search(String query, Pageable pageable);

    Flux<TrxOrderStock> search(Query query);
}

class TrxOrderStockSearchRepositoryInternalImpl implements TrxOrderStockSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxOrderStockSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxOrderStock> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxOrderStock> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxOrderStock.class).map(SearchHit::getContent);
    }
}
