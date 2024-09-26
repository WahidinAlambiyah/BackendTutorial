package com.mycompany.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.myapp.domain.TrxTournament;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TrxTournament} entity.
 */
public interface TrxTournamentSearchRepository
    extends ReactiveElasticsearchRepository<TrxTournament, Long>, TrxTournamentSearchRepositoryInternal {}

interface TrxTournamentSearchRepositoryInternal {
    Flux<TrxTournament> search(String query);

    Flux<TrxTournament> search(Query query);
}

class TrxTournamentSearchRepositoryInternalImpl implements TrxTournamentSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TrxTournamentSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TrxTournament> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Flux<TrxTournament> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TrxTournament.class).map(SearchHit::getContent);
    }
}
