package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.MstCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link MstCategory} entity.
 */
public interface MstCategorySearchRepository
    extends ReactiveElasticsearchRepository<MstCategory, Long>, MstCategorySearchRepositoryInternal {}

interface MstCategorySearchRepositoryInternal {
    Flux<MstCategory> search(String query, Pageable pageable);

    Flux<MstCategory> search(Query query);
}

class MstCategorySearchRepositoryInternalImpl implements MstCategorySearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    MstCategorySearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<MstCategory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<MstCategory> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, MstCategory.class).map(SearchHit::getContent);
    }
}
