package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.MstProvince;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MstProvince} entity.
 */
public interface MstProvinceSearchRepository
    extends ReactiveElasticsearchRepository<MstProvince, Long>, MstProvinceSearchRepositoryInternal {}

interface MstProvinceSearchRepositoryInternal {
    Flux<MstProvince> search(String query, Pageable pageable);

    Flux<MstProvince> search(Query query);
}

class MstProvinceSearchRepositoryInternalImpl implements MstProvinceSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MstProvinceSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MstProvince> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MstProvince> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MstProvince.class).map(SearchHit::getContent);
    }
}
