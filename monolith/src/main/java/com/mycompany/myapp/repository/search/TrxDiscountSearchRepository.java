package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxDiscount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxDiscount} entity.
 */
public interface TrxDiscountSearchRepository
    extends ReactiveElasticsearchRepository<TrxDiscount, Long>, TrxDiscountSearchRepositoryInternal {}

interface TrxDiscountSearchRepositoryInternal {
    Flux<TrxDiscount> search(String query, Pageable pageable);

    Flux<TrxDiscount> search(Query query);
}

class TrxDiscountSearchRepositoryInternalImpl implements TrxDiscountSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxDiscountSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxDiscount> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxDiscount> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxDiscount.class).map(SearchHit::getContent);
    }
}
