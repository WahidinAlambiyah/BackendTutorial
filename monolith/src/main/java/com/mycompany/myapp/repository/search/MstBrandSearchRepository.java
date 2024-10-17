package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.MstBrand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MstBrand} entity.
 */
public interface MstBrandSearchRepository extends ReactiveElasticsearchRepository<MstBrand, Long>, MstBrandSearchRepositoryInternal {}

interface MstBrandSearchRepositoryInternal {
    Flux<MstBrand> search(String query, Pageable pageable);

    Flux<MstBrand> search(Query query);
}

class MstBrandSearchRepositoryInternalImpl implements MstBrandSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MstBrandSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MstBrand> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MstBrand> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MstBrand.class).map(SearchHit::getContent);
    }
}
