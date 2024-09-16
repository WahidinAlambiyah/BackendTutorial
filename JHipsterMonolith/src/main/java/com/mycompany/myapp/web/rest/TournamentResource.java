package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Tournament;
import com.mycompany.myapp.repository.TournamentRepository;
import com.mycompany.myapp.repository.search.TournamentSearchRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Tournament}.
 */
@RestController
@RequestMapping("/api/tournaments")
@Transactional
public class TournamentResource {

    private static final Logger log = LoggerFactory.getLogger(TournamentResource.class);

    private static final String ENTITY_NAME = "tournament";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TournamentRepository tournamentRepository;

    private final TournamentSearchRepository tournamentSearchRepository;

    public TournamentResource(TournamentRepository tournamentRepository, TournamentSearchRepository tournamentSearchRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentSearchRepository = tournamentSearchRepository;
    }

    /**
     * {@code POST  /tournaments} : Create a new tournament.
     *
     * @param tournament the tournament to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tournament, or with status {@code 400 (Bad Request)} if the tournament has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Tournament>> createTournament(@Valid @RequestBody Tournament tournament) throws URISyntaxException {
        log.debug("REST request to save Tournament : {}", tournament);
        if (tournament.getId() != null) {
            throw new BadRequestAlertException("A new tournament cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tournamentRepository
            .save(tournament)
            .flatMap(tournamentSearchRepository::save)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/tournaments/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tournaments/:id} : Updates an existing tournament.
     *
     * @param id the id of the tournament to save.
     * @param tournament the tournament to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tournament,
     * or with status {@code 400 (Bad Request)} if the tournament is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tournament couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Tournament>> updateTournament(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Tournament tournament
    ) throws URISyntaxException {
        log.debug("REST request to update Tournament : {}, {}", id, tournament);
        if (tournament.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tournament.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tournamentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tournamentRepository
                    .save(tournament)
                    .flatMap(tournamentSearchRepository::save)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(
                        result ->
                            ResponseEntity.ok()
                                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                                .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tournaments/:id} : Partial updates given fields of an existing tournament, field will ignore if it is null
     *
     * @param id the id of the tournament to save.
     * @param tournament the tournament to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tournament,
     * or with status {@code 400 (Bad Request)} if the tournament is not valid,
     * or with status {@code 404 (Not Found)} if the tournament is not found,
     * or with status {@code 500 (Internal Server Error)} if the tournament couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Tournament>> partialUpdateTournament(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Tournament tournament
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tournament partially : {}, {}", id, tournament);
        if (tournament.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tournament.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tournamentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Tournament> result = tournamentRepository
                    .findById(tournament.getId())
                    .map(existingTournament -> {
                        if (tournament.getName() != null) {
                            existingTournament.setName(tournament.getName());
                        }
                        if (tournament.getType() != null) {
                            existingTournament.setType(tournament.getType());
                        }
                        if (tournament.getPrizeAmount() != null) {
                            existingTournament.setPrizeAmount(tournament.getPrizeAmount());
                        }
                        if (tournament.getStartDate() != null) {
                            existingTournament.setStartDate(tournament.getStartDate());
                        }
                        if (tournament.getEndDate() != null) {
                            existingTournament.setEndDate(tournament.getEndDate());
                        }
                        if (tournament.getLocation() != null) {
                            existingTournament.setLocation(tournament.getLocation());
                        }
                        if (tournament.getMaxParticipants() != null) {
                            existingTournament.setMaxParticipants(tournament.getMaxParticipants());
                        }
                        if (tournament.getStatus() != null) {
                            existingTournament.setStatus(tournament.getStatus());
                        }

                        return existingTournament;
                    })
                    .flatMap(tournamentRepository::save)
                    .flatMap(savedTournament -> {
                        tournamentSearchRepository.save(savedTournament);
                        return Mono.just(savedTournament);
                    });

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(
                        res ->
                            ResponseEntity.ok()
                                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                .body(res)
                    );
            });
    }

    /**
     * {@code GET  /tournaments} : get all the tournaments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tournaments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Tournament>> getAllTournaments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all Tournaments");
        if (eagerload) {
            return tournamentRepository.findAllWithEagerRelationships().collectList();
        } else {
            return tournamentRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /tournaments} : get all the tournaments as a stream.
     * @return the {@link Flux} of tournaments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Tournament> getAllTournamentsAsStream() {
        log.debug("REST request to get all Tournaments as a stream");
        return tournamentRepository.findAll();
    }

    /**
     * {@code GET  /tournaments/:id} : get the "id" tournament.
     *
     * @param id the id of the tournament to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tournament, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Tournament>> getTournament(@PathVariable("id") Long id) {
        log.debug("REST request to get Tournament : {}", id);
        Mono<Tournament> tournament = tournamentRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(tournament);
    }

    /**
     * {@code DELETE  /tournaments/:id} : delete the "id" tournament.
     *
     * @param id the id of the tournament to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTournament(@PathVariable("id") Long id) {
        log.debug("REST request to delete Tournament : {}", id);
        return tournamentRepository
            .deleteById(id)
            .then(tournamentSearchRepository.deleteById(id))
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /tournaments/_search?query=:query} : search for the tournament corresponding
     * to the query.
     *
     * @param query the query of the tournament search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<Tournament>> searchTournaments(@RequestParam("query") String query) {
        log.debug("REST request to search Tournaments for query {}", query);
        try {
            return tournamentSearchRepository.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
