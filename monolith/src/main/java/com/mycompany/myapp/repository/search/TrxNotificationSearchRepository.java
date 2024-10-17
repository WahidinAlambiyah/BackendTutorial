package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxNotification} entity.
 */
public interface TrxNotificationSearchRepository
    extends ReactiveElasticsearchRepository<TrxNotification, Long>, TrxNotificationSearchRepositoryInternal {}

interface TrxNotificationSearchRepositoryInternal {
    Flux<TrxNotification> search(String query, Pageable pageable);

    Flux<TrxNotification> search(Query query);
}

class TrxNotificationSearchRepositoryInternalImpl implements TrxNotificationSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxNotificationSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxNotification> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxNotification> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxNotification.class).map(SearchHit::getContent);
    }
}
