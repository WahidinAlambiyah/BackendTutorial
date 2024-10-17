package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxEventCriteria;
import com.mycompany.myapp.repository.TrxEventRepository;
import com.mycompany.myapp.service.TrxEventService;
import com.mycompany.myapp.service.dto.TrxEventDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxEvent}.
 */
@RestController
@RequestMapping("/api/trx-events")
public class TrxEventResource {

    private static final Logger log = LoggerFactory.getLogger(TrxEventResource.class);

    private static final String ENTITY_NAME = "trxEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxEventService trxEventService;

    private final TrxEventRepository trxEventRepository;

    public TrxEventResource(TrxEventService trxEventService, TrxEventRepository trxEventRepository) {
        this.trxEventService = trxEventService;
        this.trxEventRepository = trxEventRepository;
    }

    /**
     * {@code POST  /trx-events} : Create a new trxEvent.
     *
     * @param trxEventDTO the trxEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxEventDTO, or with status {@code 400 (Bad Request)} if the trxEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxEventDTO>> createTrxEvent(@Valid @RequestBody TrxEventDTO trxEventDTO) throws URISyntaxException {
        log.debug("REST request to save TrxEvent : {}", trxEventDTO);
        if (trxEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxEventService
            .save(trxEventDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-events/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-events/:id} : Updates an existing trxEvent.
     *
     * @param id the id of the trxEventDTO to save.
     * @param trxEventDTO the trxEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxEventDTO,
     * or with status {@code 400 (Bad Request)} if the trxEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxEventDTO>> updateTrxEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxEventDTO trxEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxEvent : {}, {}", id, trxEventDTO);
        if (trxEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxEventRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxEventService
                    .update(trxEventDTO)
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
     * {@code PATCH  /trx-events/:id} : Partial updates given fields of an existing trxEvent, field will ignore if it is null
     *
     * @param id the id of the trxEventDTO to save.
     * @param trxEventDTO the trxEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxEventDTO,
     * or with status {@code 400 (Bad Request)} if the trxEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxEventDTO>> partialUpdateTrxEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxEventDTO trxEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxEvent partially : {}, {}", id, trxEventDTO);
        if (trxEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxEventRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxEventDTO> result = trxEventService.partialUpdate(trxEventDTO);

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
     * {@code GET  /trx-events} : get all the trxEvents.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxEvents in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxEventDTO>>> getAllTrxEvents(
        TrxEventCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxEvents by criteria: {}", criteria);
        return trxEventService
            .countByCriteria(criteria)
            .zipWith(trxEventService.findByCriteria(criteria, pageable).collectList())
            .map(
                countWithEntities ->
                    ResponseEntity.ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /trx-events/count} : count all the trxEvents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxEvents(TrxEventCriteria criteria) {
        log.debug("REST request to count TrxEvents by criteria: {}", criteria);
        return trxEventService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-events/:id} : get the "id" trxEvent.
     *
     * @param id the id of the trxEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxEventDTO>> getTrxEvent(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxEvent : {}", id);
        Mono<TrxEventDTO> trxEventDTO = trxEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxEventDTO);
    }

    /**
     * {@code DELETE  /trx-events/:id} : delete the "id" trxEvent.
     *
     * @param id the id of the trxEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxEvent(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxEvent : {}", id);
        return trxEventService
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
     * {@code SEARCH  /trx-events/_search?query=:query} : search for the trxEvent corresponding
     * to the query.
     *
     * @param query the query of the trxEvent search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxEventDTO>>> searchTrxEvents(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxEvents for query {}", query);
        return trxEventService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxEventService.search(query, pageable)));
    }
}
