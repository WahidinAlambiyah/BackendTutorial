package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.MstCustomer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MstCustomer} entity.
 */
public interface MstCustomerSearchRepository
    extends ReactiveElasticsearchRepository<MstCustomer, Long>, MstCustomerSearchRepositoryInternal {}

interface MstCustomerSearchRepositoryInternal {
    Flux<MstCustomer> search(String query, Pageable pageable);

    Flux<MstCustomer> search(Query query);
}

class MstCustomerSearchRepositoryInternalImpl implements MstCustomerSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MstCustomerSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MstCustomer> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MstCustomer> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MstCustomer.class).map(SearchHit::getContent);
    }
}
