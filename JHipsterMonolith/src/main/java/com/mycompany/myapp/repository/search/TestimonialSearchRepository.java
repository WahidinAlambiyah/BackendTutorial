package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.Testimonial;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Testimonial} entity.
 */
public interface TestimonialSearchRepository
    extends ReactiveElasticsearchRepository<Testimonial, Long>, TestimonialSearchRepositoryInternal {}

interface TestimonialSearchRepositoryInternal {
    Flux<Testimonial> search(String query);

    Flux<Testimonial> search(Query query);
}

class TestimonialSearchRepositoryInternalImpl implements TestimonialSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TestimonialSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Testimonial> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Flux<Testimonial> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Testimonial.class).map(SearchHit::getContent);
    }
}
