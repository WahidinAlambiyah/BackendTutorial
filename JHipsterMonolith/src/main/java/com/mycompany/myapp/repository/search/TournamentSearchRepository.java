package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.Tournament;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Tournament} entity.
 */
public interface TournamentSearchRepository extends ReactiveElasticsearchRepository<Tournament, Long>, TournamentSearchRepositoryInternal {}

interface TournamentSearchRepositoryInternal {
    Flux<Tournament> search(String query);

    Flux<Tournament> search(Query query);
}

class TournamentSearchRepositoryInternalImpl implements TournamentSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TournamentSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Tournament> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Flux<Tournament> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Tournament.class).map(SearchHit::getContent);
    }
}
