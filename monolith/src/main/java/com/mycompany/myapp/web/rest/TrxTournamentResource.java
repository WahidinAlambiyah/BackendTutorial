package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxTournamentCriteria;
import com.mycompany.myapp.repository.TrxTournamentRepository;
import com.mycompany.myapp.service.TrxTournamentService;
import com.mycompany.myapp.service.dto.TrxTournamentDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxTournament}.
 */
@RestController
@RequestMapping("/api/trx-tournaments")
public class TrxTournamentResource {

    private static final Logger log = LoggerFactory.getLogger(TrxTournamentResource.class);

    private static final String ENTITY_NAME = "trxTournament";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxTournamentService trxTournamentService;

    private final TrxTournamentRepository trxTournamentRepository;

    public TrxTournamentResource(TrxTournamentService trxTournamentService, TrxTournamentRepository trxTournamentRepository) {
        this.trxTournamentService = trxTournamentService;
        this.trxTournamentRepository = trxTournamentRepository;
    }

    /**
     * {@code POST  /trx-tournaments} : Create a new trxTournament.
     *
     * @param trxTournamentDTO the trxTournamentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxTournamentDTO, or with status {@code 400 (Bad Request)} if the trxTournament has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxTournamentDTO>> createTrxTournament(@Valid @RequestBody TrxTournamentDTO trxTournamentDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxTournament : {}", trxTournamentDTO);
        if (trxTournamentDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxTournament cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxTournamentService
            .save(trxTournamentDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-tournaments/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-tournaments/:id} : Updates an existing trxTournament.
     *
     * @param id the id of the trxTournamentDTO to save.
     * @param trxTournamentDTO the trxTournamentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxTournamentDTO,
     * or with status {@code 400 (Bad Request)} if the trxTournamentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxTournamentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxTournamentDTO>> updateTrxTournament(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxTournamentDTO trxTournamentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxTournament : {}, {}", id, trxTournamentDTO);
        if (trxTournamentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxTournamentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxTournamentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxTournamentService
                    .update(trxTournamentDTO)
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
     * {@code PATCH  /trx-tournaments/:id} : Partial updates given fields of an existing trxTournament, field will ignore if it is null
     *
     * @param id the id of the trxTournamentDTO to save.
     * @param trxTournamentDTO the trxTournamentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxTournamentDTO,
     * or with status {@code 400 (Bad Request)} if the trxTournamentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxTournamentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxTournamentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxTournamentDTO>> partialUpdateTrxTournament(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxTournamentDTO trxTournamentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxTournament partially : {}, {}", id, trxTournamentDTO);
        if (trxTournamentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxTournamentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxTournamentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxTournamentDTO> result = trxTournamentService.partialUpdate(trxTournamentDTO);

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
     * {@code GET  /trx-tournaments} : get all the trxTournaments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxTournaments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TrxTournamentDTO> getAllTrxTournaments(TrxTournamentCriteria criteria) {
        log.debug("REST request to get TrxTournaments by criteria: {}", criteria);
        return trxTournamentService.findByCriteria(criteria);
    }

    /**
     * {@code GET  /trx-tournaments/count} : count all the trxTournaments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxTournaments(TrxTournamentCriteria criteria) {
        log.debug("REST request to count TrxTournaments by criteria: {}", criteria);
        return trxTournamentService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-tournaments/:id} : get the "id" trxTournament.
     *
     * @param id the id of the trxTournamentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxTournamentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxTournamentDTO>> getTrxTournament(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxTournament : {}", id);
        Mono<TrxTournamentDTO> trxTournamentDTO = trxTournamentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxTournamentDTO);
    }

    /**
     * {@code DELETE  /trx-tournaments/:id} : delete the "id" trxTournament.
     *
     * @param id the id of the trxTournamentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxTournament(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxTournament : {}", id);
        return trxTournamentService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /trx-tournaments/_search?query=:query} : search for the trxTournament corresponding
     * to the query.
     *
     * @param query the query of the trxTournament search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<TrxTournamentDTO>> searchTrxTournaments(@RequestParam("query") String query) {
        log.debug("REST request to search TrxTournaments for query {}", query);
        try {
            return trxTournamentService.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
