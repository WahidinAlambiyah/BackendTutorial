package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.MstProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MstProduct} entity.
 */
public interface MstProductSearchRepository extends ReactiveElasticsearchRepository<MstProduct, Long>, MstProductSearchRepositoryInternal {}

interface MstProductSearchRepositoryInternal {
    Flux<MstProduct> search(String query, Pageable pageable);

    Flux<MstProduct> search(Query query);
}

class MstProductSearchRepositoryInternalImpl implements MstProductSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MstProductSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MstProduct> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MstProduct> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MstProduct.class).map(SearchHit::getContent);
    }
}
