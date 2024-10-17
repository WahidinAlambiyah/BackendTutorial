package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxTestimonial;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxTestimonial} entity.
 */
public interface TrxTestimonialSearchRepository
    extends ReactiveElasticsearchRepository<TrxTestimonial, Long>, TrxTestimonialSearchRepositoryInternal {}

interface TrxTestimonialSearchRepositoryInternal {
    Flux<TrxTestimonial> search(String query, Pageable pageable);

    Flux<TrxTestimonial> search(Query query);
}

class TrxTestimonialSearchRepositoryInternalImpl implements TrxTestimonialSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxTestimonialSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxTestimonial> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxTestimonial> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxTestimonial.class).map(SearchHit::getContent);
    }
}
