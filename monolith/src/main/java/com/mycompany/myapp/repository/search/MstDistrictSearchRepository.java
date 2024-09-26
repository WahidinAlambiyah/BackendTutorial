package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.MstDistrict;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MstDistrict} entity.
 */
public interface MstDistrictSearchRepository
    extends ReactiveElasticsearchRepository<MstDistrict, Long>, MstDistrictSearchRepositoryInternal {}

interface MstDistrictSearchRepositoryInternal {
    Flux<MstDistrict> search(String query, Pageable pageable);

    Flux<MstDistrict> search(Query query);
}

class MstDistrictSearchRepositoryInternalImpl implements MstDistrictSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MstDistrictSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MstDistrict> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MstDistrict> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MstDistrict.class).map(SearchHit::getContent);
    }
}
